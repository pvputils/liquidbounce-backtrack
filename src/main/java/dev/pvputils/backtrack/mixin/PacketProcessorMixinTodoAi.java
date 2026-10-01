package dev.pvputils.backtrack.mixin;

import dev.pvputils.backtrack.BacktrackTodoAi;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PacketProcessor.class)
public abstract class PacketProcessorMixinTodoAi {
    @Inject(method = "processQueuedPackets", at = @At("TAIL"))
    private void backtrack$drain(CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (BacktrackTodoAi.ENGINE != null && (Object) this == client.packetProcessor() && client.isSameThread()) BacktrackTodoAi.ENGINE.drain();
    }
}
