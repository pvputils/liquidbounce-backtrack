package net.ccbluex.liquidbounce.fabric

import com.mojang.blaze3d.platform.InputConstants
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.config.types.group.ModeValueGroup
import net.ccbluex.liquidbounce.config.ConfigSystem
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.modules.combat.backtrack.ModuleBacktrack
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

/** Native controls; Backtrack and its settings/packet/render implementations remain upstream source. */
class BacktrackFabricTodoAi : ClientModInitializer {
    override fun onInitializeClient() {
        System.setProperty("net.ccbluex.liquidbounce.browser.skip", "true")
        System.setProperty("net.ccbluex.liquidbounce.interop.skip", "true")
        System.setProperty("net.ccbluex.liquidbounce.ui.basicMode", "true")
        registerBind()
        registerCommands()
    }

    private fun status() = "Backtrack " + if (ModuleBacktrack.enabled) "on" else "off"

    private fun setState(mode: String) {
        ModuleBacktrack.enabled = if (mode == "toggle") !ModuleBacktrack.enabled else mode == "on"
        ConfigSystem.store(ModuleManager.modulesConfig)
    }

    private fun registerBind() {
        val category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("backtrack", "controls"))
        val toggle = KeyMappingHelper.registerKeyMapping(
            KeyMapping("key.backtrack.toggle", InputConstants.Type.KEYBOARD, InputConstants.KEY_B, category)
        )
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (toggle.consumeClick()) {
                if (LiquidBounce.isInitialized && client.player != null) {
                    setState("toggle")
                    client.player!!.sendOverlayMessage(Component.literal(status()))
                }
            }
        }
    }

    private fun registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            val command = ClientCommands.literal("backtrack").executes { context ->
                context.source.sendFeedback(Component.literal(status()))
                1
            }
            for (mode in listOf("on", "off", "toggle")) {
                command.then(ClientCommands.literal(mode).executes { context ->
                    setState(mode)
                    context.source.sendFeedback(Component.literal(status()))
                    1
                })
            }
            dispatcher.register(command)
            val visual = ClientCommands.literal("backtrackvisual").executes { context ->
                val esp = ModuleBacktrack.settings.getValue("Esp") as ModeValueGroup<*>
                esp.setByString(if (esp.activeMode.name == "None") "Wireframe" else "None")
                ConfigSystem.store(ModuleManager.modulesConfig)
                context.source.sendFeedback(Component.literal("Backtrack visual: ${esp.activeMode.name}"))
                1
            }
            for (mode in listOf("Box", "Model", "Wireframe", "None")) {
                visual.then(ClientCommands.literal(mode.lowercase()).executes { context ->
                    ModuleBacktrack.settings.getValue("Esp").setByString(mode)
                    ConfigSystem.store(ModuleManager.modulesConfig)
                    context.source.sendFeedback(Component.literal("Backtrack visual: $mode"))
                    1
                })
            }
            dispatcher.register(visual)
        }
    }
}
