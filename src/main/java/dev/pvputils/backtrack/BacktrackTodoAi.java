package dev.pvputils.backtrack;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BacktrackTodoAi implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("backtrack");
    public static BacktrackEngineTodoAi ENGINE;
    @Override public void onInitializeClient() {
        ENGINE = new BacktrackEngineTodoAi();
        KeyMapping toggle = KeyMappingHelper.registerKeyMapping(new KeyMapping("key.backtrack.toggle", InputConstants.Type.KEYBOARD,
            InputConstants.KEY_B, KeyMapping.Category.register(Identifier.fromNamespaceAndPath("backtrack", "controls"))));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggle.consumeClick()) { ENGINE.enabled(!ENGINE.config.enabled); feedback("Backtrack " + (ENGINE.config.enabled ? "on" : "off")); }
            ENGINE.tick();
        });
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (level.isClientSide() && player == Minecraft.getInstance().player) ENGINE.attack(entity);
            return InteractionResult.PASS;
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> {
            var root = ClientCommands.literal("backtrack")
                .executes(context -> { context.getSource().sendFeedback(Component.literal(ENGINE.config.describe() + "\nQueued: " + ENGINE.queued())); return 1; });
            for (String mode : new String[]{"on", "off", "toggle"}) root.then(ClientCommands.literal(mode).executes(context -> {
                ENGINE.enabled(mode.equals("toggle") ? !ENGINE.config.enabled : mode.equals("on"));
                context.getSource().sendFeedback(Component.literal("Backtrack " + (ENGINE.config.enabled ? "on" : "off"))); return 1;
            }));
            root.then(ClientCommands.literal("set").then(ClientCommands.argument("setting", StringArgumentType.word())
                .suggests((context, builder) -> { for (String key : new String[]{"rangeMin","rangeMax","delayMin","delayMax","cooldownMin","cooldownMax","trackingBuffer","chance","attackWindow","hurtThreshold","pauseOnHurt","targetMode","targetMobs"}) builder.suggest(key); return builder.buildFuture(); })
                .then(ClientCommands.argument("value", StringArgumentType.word()).executes(context -> {
                    try { ENGINE.configure(ENGINE.config.changed(StringArgumentType.getString(context,"setting"), StringArgumentType.getString(context,"value")));
                        context.getSource().sendFeedback(Component.literal("Backtrack settings saved")); return 1;
                    } catch (IllegalArgumentException exception) { context.getSource().sendError(Component.literal(exception.getMessage())); return 0; }
                }))));
            dispatcher.register(root);
        });
        LOGGER.info("Standalone Backtrack initialized (Minecraft 26.3 / Java 25)");
    }
    private static void feedback(String message) {
        var player = Minecraft.getInstance().player;
        if (player != null) player.sendOverlayMessage(Component.literal(message));
    }
}
