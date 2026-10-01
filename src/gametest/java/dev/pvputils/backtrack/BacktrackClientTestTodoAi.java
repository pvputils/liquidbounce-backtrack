package dev.pvputils.backtrack;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.pig.Pig;
import java.util.concurrent.atomic.AtomicInteger;

public final class BacktrackClientTestTodoAi implements FabricClientGameTest {
    private static Pig target;
    private static final AtomicInteger HANDLED = new AtomicInteger();
    private static final class Probe implements Packet<ClientGamePacketListener> {
        public PacketType<? extends Packet<ClientGamePacketListener>> type() { return GamePacketTypes.CLIENTBOUND_SET_TIME; }
        public void handle(ClientGamePacketListener listener) { HANDLED.incrementAndGet(); }
    }
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    private static void dispatch(Packet<?> packet, PacketListener listener) {
        try {
        Class<?> type = Class.forName("net.minecraft.network.PacketProcessor$ListenerAndPacket");
        var constructor = type.getDeclaredConstructor(PacketListener.class, Packet.class); constructor.setAccessible(true);
        Object entry = constructor.newInstance(listener, packet);
        var method = type.getDeclaredMethod("handle"); method.setAccessible(true); method.invoke(entry);
        } catch (ReflectiveOperationException exception) { throw new AssertionError("Packet dispatch failed", exception); }
    }
    @Override public void runTest(ClientGameTestContext context) {
        check(BacktrackTodoAi.ENGINE != null, "Fabric entrypoint did not initialize");
        try (var world = context.worldBuilder().create()) {
            context.waitFor(client -> client.player != null && client.player.tickCount > 20);
            context.runOnClient(client -> {
                var engine = BacktrackTodoAi.ENGINE;
                engine.tick();
                ConfigTodoAi config = new ConfigTodoAi(); config.enabled = true; config.chance = 100;
                config.targetMobs = true; config.rangeMin = 0; config.rangeMax = 10;
                config.delayMin = config.delayMax = 100; config.cooldownMin = config.cooldownMax = 0; config.attackWindow = 5000; engine.configure(config);
                Pig pig = EntityTypes.PIG.create(client.level, EntitySpawnReason.COMMAND);
                check(pig != null, "Pig creation failed");
                pig.setPos(client.player.getX() + 2, client.player.getY(), client.player.getZ());
                pig.setId(123456789); client.level.addEntity(pig); target = pig; engine.attack(pig);
                dispatch(new Probe(), client.getConnection());
                check(engine.queued() == 1 && HANDLED.get() == 0, "Incoming packet was not delayed by runtime mixin");
            });
            context.waitFor(client -> HANDLED.get() == 1);
            context.runOnClient(client -> {
                var engine = BacktrackTodoAi.ENGINE;
                check(HANDLED.get() == 1, "Due probe packet was not replayed");
                dispatch(new Probe(), client.getConnection());
                check(engine.queued() > 0 && HANDLED.get() == 1, "Second packet was not queued");
                engine.enabled(false);
                check(engine.queued() == 0 && HANDLED.get() == 2, "Disable did not flush queued packets");
                dispatch(new Probe(), client.getConnection());
                check(HANDLED.get() == 3, "Disabled mod intercepted a packet");
                engine.enabled(true);
                var pig = client.level.entitiesForRendering().iterator();
                while (pig.hasNext()) { var entity = pig.next(); if (entity instanceof Pig) { engine.attack(entity); break; } }
                dispatch(new Probe(), client.getConnection());
                check(engine.queued() > 0 && HANDLED.get() == 3, "Disconnect probe was not queued");
                var closer = new net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket(target.getId(),
                    net.minecraft.world.entity.PositionPath.of(client.player.position().add(0.5, 0, 0)), 0, 0, true);
                check(!engine.intercept(closer, client.getConnection()), "Closer target movement was delayed");
                check(engine.queued() == 0 && HANDLED.get() == 4, "Closer movement did not flush older packets");
                dispatch(new Probe(), client.getConnection());
                engine.disconnect(); check(engine.queued() == 0 && HANDLED.get() == 4, "Disconnect replayed stale packets");
                engine.enabled(false);
            });
        }
        context.runOnClient(client -> check(BacktrackTodoAi.ENGINE.queued() == 0, "Queue survived world unload"));
        BacktrackTodoAi.LOGGER.info("Backtrack client integration checks passed: delay, replay, disable, closer-target flush, disconnect");
    }
}
