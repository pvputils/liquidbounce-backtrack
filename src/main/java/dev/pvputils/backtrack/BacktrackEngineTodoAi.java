/*
 * Backtrack behavior derived from LiquidBounce, Copyright (c) 2015-2026 CCBlueX.
 * Distributed under GPL-3.0-or-later; see LICENSE.
 */
package dev.pvputils.backtrack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.ThreadLocalRandom;

/** All methods run on the client thread. Only incoming gameplay packets are delayed. */
public final class BacktrackEngineTodoAi {
    private record Pending(Packet<?> packet, ClientPacketListener listener, ClientLevel level) {}
    private final Minecraft client = Minecraft.getInstance();
    private final DelayedQueueTodoAi<Pending> queue = new DelayedQueueTodoAi<>();
    private final VecDeltaCodec codec = new VecDeltaCodec();
    private Entity target;
    private ClientLevel world;
    private ClientPacketListener connection;
    private long lastAttack = Long.MIN_VALUE, lastInRange, cooldownUntil;
    private boolean chancePassed;
    private int delay;
    public ConfigTodoAi config = ConfigTodoAi.load();
    private static long now() { return System.nanoTime() / 1_000_000; }
    private static int random(int min, int max) { return ThreadLocalRandom.current().nextInt(min, max + 1); }
    public int queued() { return queue.size(); }
    public void configure(ConfigTodoAi next) { clear(true); config = next; config.save(); }
    public void enabled(boolean value) { clear(true); config.enabled = value; config.save(); }
    public void disconnect() { queue.clear(); target = null; world = null; connection = null; lastAttack = Long.MIN_VALUE; }
    private boolean valid(Entity entity) {
        return entity instanceof LivingEntity && entity != client.player && entity.isAlive()
            && !entity.isSpectator() && !entity.isInvisible() && (config.targetMobs || entity instanceof Player);
    }
    private double distance(Entity entity) { return Math.sqrt(entity.getBoundingBox().distanceToSqr(client.player.getBoundingBox())); }
    private boolean inRange(Entity entity) { double d = distance(entity); return d >= config.rangeMin && d <= config.rangeMax; }
    private boolean active(long time) {
        if (!config.enabled || target == null || client.player == null || client.level == null || !valid(target)
            || client.player.tickCount <= 10 || !chancePassed || time < cooldownUntil
            || lastAttack == Long.MIN_VALUE || time - lastAttack >= config.attackWindow) return false;
        if (inRange(target)) lastInRange = time;
        return time - lastInRange <= config.trackingBuffer
            && !(config.pauseOnHurt && ((LivingEntity) target).hurtTime >= config.hurtThreshold);
    }
    private void select(Entity entity, long time) {
        if (!valid(entity) || !inRange(entity)) return;
        if (entity != target) { queue.flush(this::replay); target = entity; codec.setBase(entity.getPositionCodec().getBase()); }
        lastInRange = time;
    }
    public void attack(Entity entity) {
        if (!config.enabled || client.player == null) return;
        lastAttack = now(); chancePassed = random(1, 100) <= config.chance;
        if (config.targetMode.equals("attack")) select(entity, lastAttack);
    }
    public void tick() {
        if (client.level != world || client.getConnection() != connection) {
            disconnect(); world = client.level; connection = client.getConnection();
        }
        if (client.player == null || client.level == null) return;
        long time = now();
        if (config.enabled && config.targetMode.equals("range")) {
            Entity closest = null; double best = Double.MAX_VALUE;
            for (Entity entity : client.level.entitiesForRendering()) {
                if (valid(entity) && inRange(entity) && distance(entity) < best) { closest = entity; best = distance(entity); }
            }
            if (closest != null) select(closest, time); else clear(true);
        }
        drain();
    }
    public void drain() {
        if (client.level != world || client.getConnection() != connection) { disconnect(); return; }
        long time = now();
        if (active(time)) queue.drain(time, delay, this::replay); else clear(true);
        if (queue.isEmpty()) delay = random(config.delayMin, config.delayMax);
    }
    private void clear(boolean replay) {
        if (replay) queue.flush(this::replay); else queue.clear();
        if (target != null) cooldownUntil = now() + random(config.cooldownMin, config.cooldownMax);
        target = null;
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void replay(Pending pending) {
        if (client.level != pending.level || client.getConnection() != pending.listener || !pending.listener.isAcceptingMessages()) return;
        if (!pending.listener.shouldHandleMessage(pending.packet)) return;
        try { ((Packet) pending.packet).handle(pending.listener); }
        catch (Exception exception) { pending.listener.onPacketError(pending.packet, exception); }
    }
    public boolean intercept(Packet<?> packet, ClientPacketListener listener) {
        if (client.level != world || listener != connection) { disconnect(); world = client.level; connection = listener; }
        if (packet instanceof ClientboundBundlePacket bundle) {
            if (!active(now()) && queue.isEmpty()) return false;
            for (Packet<?> child : bundle.subPackets()) {
                if (!intercept(child, listener)) replay(new Pending(child, listener, client.level));
            }
            return true;
        }
        if (packet.isTerminal() || packet instanceof ClientboundPlayerPositionPacket || packet instanceof ClientboundRespawnPacket
            || packet instanceof ClientboundLoginPacket || packet instanceof ClientboundDisconnectPacket
            || packet instanceof ClientboundSetHealthPacket health && health.getHealth() <= 0) { clear(true); return false; }
        if (packet instanceof ClientboundSystemChatPacket || packet instanceof ClientboundPlayerChatPacket
            || packet instanceof ClientboundDisguisedChatPacket || packet instanceof ClientboundKeepAlivePacket
            || packet instanceof ClientboundPingPacket
            || packet instanceof ClientboundSoundPacket sound && sound.getSound().value() == net.minecraft.sounds.SoundEvents.PLAYER_HURT) return false;
        if (!active(now())) { clear(true); return false; }
        Vec3 pos = null;
        if (packet instanceof ClientboundMoveEntityPacket movement && movement.getEntity(client.level) == target && movement.hasPosition())
            pos = movement.getPositionDelta().decode(codec).endPosition();
        else if (packet instanceof ClientboundTeleportEntityPacket teleport && teleport.id() == target.getId()) pos = net.minecraft.world.entity.PositionMoveRotation.calculateAbsolute(
            new net.minecraft.world.entity.PositionMoveRotation(codec.getBase(), target.getDeltaMovement(), target.getYRot(), target.getXRot()),
            teleport.change(), teleport.relatives()).position();
        else if (packet instanceof ClientboundEntityPositionSyncPacket sync && sync.id() == target.getId()) pos = sync.position().endPosition();
        if (pos != null) {
            codec.setBase(pos);
            if (target.getBoundingBox().move(pos.subtract(target.position())).distanceToSqr(client.player.getBoundingBox())
                < target.getBoundingBox().distanceToSqr(client.player.getBoundingBox())) { queue.flush(this::replay); return false; }
        }
        if (queue.size() >= 4096) { clear(true); return false; }
        if (queue.isEmpty()) delay = random(config.delayMin, config.delayMax);
        queue.add(new Pending(packet, listener, client.level), now()); return true;
    }
}
