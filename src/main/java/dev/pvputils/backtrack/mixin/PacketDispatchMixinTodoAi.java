package dev.pvputils.backtrack.mixin;

import dev.pvputils.backtrack.BacktrackTodoAi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.PacketProcessor$ListenerAndPacket")
public abstract class PacketDispatchMixinTodoAi {
    @Shadow @Final private PacketListener listener;
    @Shadow @Final private Packet<?> packet;
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private void backtrack$receive(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (BacktrackTodoAi.ENGINE != null && client.isSameThread() && client.player != null && client.level != null
            && listener == client.getConnection() && listener.shouldHandleMessage(packet)
            && BacktrackTodoAi.ENGINE.intercept(packet, (ClientPacketListener) listener)) ci.cancel();
    }
}
