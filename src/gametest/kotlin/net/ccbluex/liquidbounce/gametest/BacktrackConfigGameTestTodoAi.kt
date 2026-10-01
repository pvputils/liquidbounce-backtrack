package net.ccbluex.liquidbounce.gametest

import net.ccbluex.liquidbounce.config.ConfigSystem
import net.ccbluex.liquidbounce.config.types.BindValue
import net.ccbluex.liquidbounce.fabric.BacktrackConfigScreenTodoAi
import net.ccbluex.liquidbounce.fabric.BacktrackModMenuTodoAi
import net.ccbluex.liquidbounce.features.global.GlobalManager
import net.ccbluex.liquidbounce.features.global.GlobalSettingsTarget
import net.ccbluex.liquidbounce.features.misc.FriendManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.modules.combat.backtrack.ModuleBacktrack
import net.ccbluex.liquidbounce.utils.input.InputBind
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.input.MouseButtonInfo

/** Exercises the actual native widgets and persistence, without changing original module code. */
class BacktrackConfigGameTestTodoAi {
    private fun screen() = Minecraft.getInstance().gui.screen()!!
    private fun click(label: String) {
        if (screen().children().filterIsInstance<Button>().none { it.message.string.startsWith(label) }) {
            while (true) {
                val previous = screen().children().filterIsInstance<Button>()
                    .firstOrNull { it.message.string == "Previous" && it.active } ?: break
                previous.onPress(MouseButtonInfo(0, 0))
            }
        }
        repeat(10) {
            val buttons = screen().children().filterIsInstance<Button>()
            val match = buttons.firstOrNull { it.message.string.startsWith(label) }
            if (match != null) {
                check(match.active)
                match.onPress(MouseButtonInfo(0, 0))
                return
            }
            val next = buttons.firstOrNull { it.message.string == "Next" && it.active }
                ?: error("Missing menu entry $label")
            next.onPress(MouseButtonInfo(0, 0))
        }
        error("Missing menu entry $label")
    }

    fun runTest(context: ClientGameTestContext) {
        context.onClient { Minecraft.getInstance().gui.setScreen(BacktrackConfigScreenTodoAi()) }
        context.takeScreenshot("backtrack-config")
        checkRange(context)
        checkBindings(context)
        checkVisuals(context)
        checkTargets(context)
        checkFriends(context)
        context.onClient { Minecraft.getInstance().gui.setScreen(null) }
    }

    private fun checkBindings(context: ClientGameTestContext) = context.onClient {
        check(BacktrackModMenuTodoAi().modConfigScreenFactory.create(null) is BacktrackConfigScreenTodoAi)
        click("Backtrack settings")
        click("Module binding")
        val value = ModuleBacktrack.settings.getValue("Bind") as BindValue
        val original = value.get()
        click("Key:")
        screen().children().filterIsInstance<EditBox>().single().value = "g"
        click("Apply")
        click("Hold")
        click("Shift:")
        check(value.get().boundKey.name == "key.keyboard.g")
        check(value.get().action == InputBind.BindAction.HOLD)
        check(InputBind.Modifier.SHIFT in value.get().modifiers)
        value.set(original)
        ConfigSystem.store(ModuleManager.modulesConfig)
        click("Back")
        click("Back")
        click("Minecraft key bindings")
        check(screen().javaClass.simpleName == "ControlsScreen")
        screen().onClose()
    }

    private fun checkRange(context: ClientGameTestContext) {
        context.onClient {
            click("Backtrack settings")
            click("Range:")
            val range = ModuleBacktrack.settings.getValue("Range")
            val original = range.get().toString()
            val input = screen().children().filterIsInstance<EditBox>().single()
            input.value = "999..1000"
            click("Apply")
            check(range.get().toString() == original)
            input.value = "4..1"
            click("Apply")
            check(range.get().toString() == original)
            input.value = "1..4"
            click("Apply")
            check(range.get().toString() == "1.0..4.0")
            range.restore()
            ConfigSystem.load(ModuleManager.modulesConfig)
            check(range.get().toString() == "1.0..4.0")
            range.setByString(original)
            ConfigSystem.store(ModuleManager.modulesConfig)
        }
        context.takeScreenshot("backtrack-settings")
        context.onClient { click("Back") }
    }

    private fun checkVisuals(context: ClientGameTestContext) {
        context.onClient {
            click("Visuals")
            click("Esp:")
            click("Box")
            click("Box options")
            click("Color:")
        }
        context.takeScreenshot("backtrack-color-editor")
        context.onClient {
            val input = screen().children().filterIsInstance<EditBox>().single()
            input.value = "#80112233"
            click("Apply")
            check(screen().children().filterIsInstance<Button>().any { it.message.string.contains("#80112233") })
            click("Back")
            click("Model options")
            click("Light Percent:")
            screen().children().filterIsInstance<EditBox>().single().value = "70"
            click("Apply")
            click("Back")
            click("Esp:")
            click("Wireframe")
            click("Back")
        }
    }

    private fun checkTargets(context: ClientGameTestContext) {
        context.onClient {
            click("Target filters")
            click("Combat filters")
            val choices = GlobalSettingsTarget.combatChoices
            val player = choices.choices.first { it.tag == "Players" }
            val before = player in choices
            click("Players:")
            check((player in choices) != before)
            ConfigSystem.load(GlobalManager)
            check((player in choices) != before)
            click("Players:")
            check((player in choices) == before)
        }
        context.takeScreenshot("backtrack-target-filters")
        context.onClient { click("Back"); click("Back") }
    }

    private fun checkFriends(context: ClientGameTestContext) = context.onClient {
        click("Friend exclusions")
        click("Add friend")
        val fields = screen().children().filterIsInstance<EditBox>()
        fields[0].value = "ConfigTestFriend"
        fields[1].value = "Test alias"
        click("Save")
        check(FriendManager.isFriend("ConfigTestFriend"))
        ConfigSystem.load(FriendManager)
        check(FriendManager.friends.first { it.name == "ConfigTestFriend" }.alias == "Test alias")
        click("ConfigTestFriend")
        click("Remove")
        check(!FriendManager.isFriend("ConfigTestFriend"))
        click("Back")
    }
}
