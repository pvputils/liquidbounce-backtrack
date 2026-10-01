package net.ccbluex.liquidbounce.fabric

import net.ccbluex.liquidbounce.config.ConfigSystem
import net.ccbluex.liquidbounce.config.types.RangedValue
import net.ccbluex.liquidbounce.config.types.BindValue
import net.ccbluex.liquidbounce.config.types.Value
import net.ccbluex.liquidbounce.config.types.group.ModeValueGroup
import net.ccbluex.liquidbounce.config.types.group.ValueGroup
import net.ccbluex.liquidbounce.config.types.list.ChoiceListValue
import net.ccbluex.liquidbounce.config.types.list.MultiChoiceListValue
import net.ccbluex.liquidbounce.config.types.list.Tagged
import net.ccbluex.liquidbounce.features.blink.BlinkManager
import net.ccbluex.liquidbounce.features.global.GlobalManager
import net.ccbluex.liquidbounce.features.global.GlobalSettingsTarget
import net.ccbluex.liquidbounce.features.misc.FriendManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.modules.combat.backtrack.ModuleBacktrack
import net.ccbluex.liquidbounce.render.engine.type.Color4b
import net.ccbluex.liquidbounce.utils.input.InputBind
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.options.controls.ControlsScreen
import net.minecraft.network.chat.Component

/** Native menu over the upstream value tree, including inactive modes and nested groups. */
open class BacktrackConfigScreenTodoAi(
    private val parentScreen: Screen? = null,
    titleText: String = "Backtrack configuration",
    private val entries: () -> List<Entry> = ::rootEntries,
) : Screen(Component.literal(titleText)) {
    data class Entry(val label: () -> String, val action: (Screen) -> Unit, val hint: String = "")
    private var page = 0
    private val pageSize get() = ((height - 110) / 24).coerceAtLeast(1)

    override fun init() {
        val rows = entries()
        val pages = ((rows.size + pageSize - 1) / pageSize).coerceAtLeast(1)
        page = page.coerceIn(0, pages - 1)
        rows.drop(page * pageSize).take(pageSize).forEachIndexed { index, entry ->
            val button = Button.builder(Component.literal(entry.label())) { entry.action(this) }
                .bounds(width / 2 - 150, 48 + index * 24, 300, 20)
                .tooltip(Tooltip.create(Component.literal(entry.hint))).build()
            addRenderableWidget(button)
        }
        addRenderableWidget(Button.builder(Component.literal("Previous")) { page--; rebuildWidgets() }
            .bounds(width / 2 - 150, height - 52, 95, 20).build().apply { active = page > 0 })
        addRenderableWidget(Button.builder(Component.literal("${page + 1} / $pages")) { }
            .bounds(width / 2 - 50, height - 52, 100, 20).build().apply { active = false })
        addRenderableWidget(Button.builder(Component.literal("Next")) { page++; rebuildWidgets() }
            .bounds(width / 2 + 55, height - 52, 95, 20).build().apply { active = page + 1 < pages })
        addRenderableWidget(Button.builder(Component.literal(if (parentScreen == null) "Done" else "Back")) {
            onClose()
        }.bounds(width / 2 - 75, height - 27, 150, 20).build())
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.extractRenderState(context, mouseX, mouseY, partialTick)
        context.centeredText(font, title, width / 2, 16, -1)
        context.centeredText(font, "Changes save immediately; hover for details", width / 2, 32, 0xffaaaaaa.toInt())
    }

    override fun onClose() { open(parentScreen) }

    @Suppress("TooManyFunctions")
    companion object {
        private fun open(screen: Screen?) = Minecraft.getInstance().gui.setScreen(screen)
        private fun refresh(screen: Screen) {
            (screen as BacktrackConfigScreenTodoAi).rebuildWidgets()
        }
        private fun label(name: String) = name.replace(Regex("([a-z])([A-Z])"), "$1 $2")
        private fun save() {
            ConfigSystem.store(ModuleManager.modulesConfig)
            ConfigSystem.store(GlobalManager)
            ConfigSystem.store(FriendManager)
        }

        private fun rootEntries(): List<Entry> = listOf(
            groupEntry("Backtrack settings", ModuleBacktrack),
            groupEntry("Visuals (all modes)", ModuleBacktrack.settings.getValue("Esp") as ValueGroup),
            groupEntry("Target filters", GlobalSettingsTarget),
            groupEntry("Shared packet settings", BlinkManager),
            Entry({ "Friend exclusions" }, { parent -> open(friendsScreen(parent)) }),
            Entry({ "Minecraft key bindings" }, { parent ->
                val client = Minecraft.getInstance()
                open(ControlsScreen(parent, client.options))
            }),
        )

        private fun groupEntry(name: String, group: ValueGroup) = Entry({ "$name >" }, { parent ->
            open(BacktrackConfigScreenTodoAi(parent, name) { groupEntries(group) })
        })

        internal fun groupEntries(group: ValueGroup): List<Entry> {
            val rows = if (group is ModeValueGroup<*>) {
                mutableListOf()
            } else {
                group.containedValues.map { valueEntry(it) }.toMutableList()
            }
            if (group is ModeValueGroup<*>) {
                rows.add(0, choiceEntry(group, group.getModeStrings().toList()))
                rows.addAll(group.modes.map { groupEntry("${label(it.name)} options", it) })
            }
            return rows
        }

        private fun valueEntry(value: Value<*>): Entry = when {
            value is ValueGroup -> groupEntry(label(value.name), value)
            value is ChoiceListValue<*> -> choiceEntry(value, value.getChoicesStrings().toList())
            value is MultiChoiceListValue<*> -> multiEntry(value)
            value is BindValue -> bindingEntry(value)
            value.get() is Boolean -> Entry({ "${label(value.name)}: ${if (value.get() == true) "On" else "Off"}" }, {
                value.setByString((value.get() != true).toString()); save()
                refresh(it)
            }, value.description.get().orEmpty())
            else -> Entry({ "${label(value.name)}: ${format(value)}" }, { parent ->
                open(ValueEditorTodoAi(parent, value))
            }, value.description.get().orEmpty())
        }

        private fun choiceEntry(value: Value<*>, choices: List<String>) = Entry(
            { "${label(value.name)}: ${value.getValue()} >" }, { parent ->
                open(BacktrackConfigScreenTodoAi(parent, label(value.name)) {
                    choices.map { choice -> Entry({
                        "$choice${if (value.getValue() == choice) " (selected)" else ""}"
                    }, {
                        value.setByString(choice); save(); open(parent)
                    }) }
                })
            }, value.description.get().orEmpty()
        )

        @Suppress("UNCHECKED_CAST")
        private fun multiEntry(value: MultiChoiceListValue<*>) = Entry({ "${label(value.name)} filters >" }, { parent ->
            val typed = value as MultiChoiceListValue<Tagged>
            open(BacktrackConfigScreenTodoAi(parent, label(value.name)) {
                typed.choices.map { choice -> Entry({ "${choice.tag}: ${if (choice in typed) "On" else "Off"}" }, {
                    typed.toggle(choice); save(); refresh(it)
                }) }
            })
        })

        private fun friendsScreen(parent: Screen) = BacktrackConfigScreenTodoAi(parent, "Friend exclusions") {
            listOf(valueEntry(FriendManager.containedValues.first { it.name == "CancelAttack" }),
                Entry({ "Add friend" }, { open(FriendEditorTodoAi(it)) })) +
                FriendManager.friends.map { friend -> Entry({ "${friend.name} >" }, {
                    open(FriendEditorTodoAi(it, friend))
                }, "Friend targets are excluded from Backtrack.") }
        }

        private fun bindingEntry(value: BindValue) = Entry({ "Module binding >" }, { parent ->
            open(BacktrackConfigScreenTodoAi(parent, "Original module binding") {
                listOf(Entry({ "Key: ${format(value)}" }, { open(ValueEditorTodoAi(it, value)) })) +
                    InputBind.BindAction.entries.map { action -> Entry({
                        "${action.tag}${if (value.get().action == action) " (selected)" else ""}"
                    }, {
                        value.set(value.get().copy(action = action)); save(); refresh(it)
                    }) } + InputBind.Modifier.entries.map { modifierEntry(value, it) }
            })
        })

        private fun modifierEntry(value: BindValue, modifier: InputBind.Modifier) = Entry({
            "${modifier.tag}: ${if (modifier in value.get().modifiers) "On" else "Off"}"
        }, {
            val current = value.get()
            val modifiers = current.modifiers.toMutableSet()
            if (!modifiers.remove(modifier)) modifiers.add(modifier)
            value.set(current.copy(modifiers = modifiers)); save(); refresh(it)
        })

        internal fun format(value: Value<*>): String = when (val current = value.get()) {
            is Color4b -> "#${current.toHexString()}"
            is InputBind -> if (current.isUnbound) "NONE" else current.boundKey.name
            is ClosedRange<*> -> "${current.start}..${current.endInclusive}"
            else -> current.toString()
        }

        internal fun apply(value: Value<*>, text: String) {
            if (value is RangedValue<*>) {
                val parts = text.split("..").map { it.toDouble() }
                val bounds = value.range.start.toString().toDouble()..value.range.endInclusive.toString().toDouble()
                require(parts.all { it.isFinite() && it in bounds }) { "Use values within ${value.range}." }
                require(parts.size == if (value.get() is ClosedRange<*>) 2 else 1) { "Use min..max for a range." }
                require(parts.first() <= parts.last()) { "Minimum must not exceed maximum." }
            }
            value.setByString(text.trim())
            save()
        }

        internal fun reset(value: Value<*>) { value.restore(); save() }
    }
}

private class ValueEditorTodoAi(private val parentScreen: Screen, private val value: Value<*>) :
    Screen(Component.literal(value.name)) {
    private lateinit var input: EditBox
    private var draft = BacktrackConfigScreenTodoAi.format(value)
    private var error = ""

    override fun init() {
        input = addRenderableWidget(EditBox(font, width / 2 - 150, height / 2 - 12, 300, 20, title))
        input.setMaxLength(256)
        input.value = draft
        input.setResponder { draft = it; error = "" }
        addRenderableWidget(Button.builder(Component.literal("Apply")) {
            runCatching { BacktrackConfigScreenTodoAi.apply(value, draft) }
                .onSuccess { onClose() }.onFailure { error = it.message ?: "Invalid value" }
        }.bounds(width / 2 - 150, height / 2 + 18, 95, 20).build())
        addRenderableWidget(Button.builder(Component.literal("Reset default")) {
            BacktrackConfigScreenTodoAi.reset(value); input.value = BacktrackConfigScreenTodoAi.format(value)
        }.bounds(width / 2 - 50, height / 2 + 18, 100, 20).build())
        addRenderableWidget(Button.builder(Component.literal("Cancel")) { onClose() }
            .bounds(width / 2 + 55, height / 2 + 18, 95, 20).build())
        setInitialFocus(input)
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.extractRenderState(context, mouseX, mouseY, partialTick)
        context.centeredText(font, title, width / 2, height / 2 - 60, -1)
        val hint = when (value) {
            is RangedValue<*> -> "Allowed: ${value.range} ${value.suffix}; ranges use min..max"
            is BindValue -> "Key name: b, key.mouse.left, or NONE to unbind"
            else -> if (value.get() is Color4b) "Hex color: #RRGGBB or #AARRGGBB (alpha first)" else "Enter a value"
        }
        context.centeredText(font, hint, width / 2, height / 2 - 36, 0xffaaaaaa.toInt())
        context.centeredText(font, font.plainSubstrByWidth(error, width - 30), width / 2, height / 2 + 48,
            0xffff7777.toInt())
    }

    override fun onClose() { Minecraft.getInstance().gui.setScreen(parentScreen) }
}

private class FriendEditorTodoAi(
    private val parentScreen: Screen,
    private val friend: FriendManager.Friend? = null,
) : Screen(Component.literal("Friend exclusion")) {
    private var name = friend?.name.orEmpty()
    private var alias = friend?.alias.orEmpty()
    private var error = ""
    override fun init() {
        val input = addRenderableWidget(EditBox(font, width / 2 - 150, height / 2 - 25, 300, 20, title))
        input.setMaxLength(16)
        input.value = name
        input.setResponder { name = it; error = "" }
        val aliasInput = addRenderableWidget(EditBox(font, width / 2 - 150, height / 2 + 10, 300, 20,
            Component.literal("Alias")))
        aliasInput.setMaxLength(64)
        aliasInput.value = alias
        aliasInput.setResponder { alias = it }
        addRenderableWidget(Button.builder(Component.literal("Save")) {
            if (name.matches(Regex("[A-Za-z0-9_]{1,16}"))) {
                friend?.let { FriendManager.remove(it.name) }
                FriendManager.remove(name)
                FriendManager.add(FriendManager.Friend(name, alias.takeIf { it.isNotBlank() }))
                ConfigSystem.store(FriendManager)
                onClose()
            } else {
                error = "Use 1-16 letters, numbers or underscores."
            }
        }.bounds(width / 2 - 150, height / 2 + 40, 95, 20).build())
        addRenderableWidget(Button.builder(Component.literal("Remove")) {
            friend?.let { FriendManager.remove(it.name) }
            ConfigSystem.store(FriendManager)
            onClose()
        }.bounds(width / 2 - 50, height / 2 + 40, 100, 20).build().apply { active = friend != null })
        addRenderableWidget(Button.builder(Component.literal("Cancel")) { onClose() }
            .bounds(width / 2 + 55, height / 2 + 40, 95, 20).build())
        setInitialFocus(input)
    }

    override fun extractRenderState(context: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.extractRenderState(context, mouseX, mouseY, partialTick)
        context.centeredText(font, "Minecraft player name", width / 2, height / 2 - 40, -1)
        context.centeredText(font, "Optional alias", width / 2, height / 2 - 2, -1)
        context.centeredText(font, error, width / 2, height / 2 + 67, 0xffff7777.toInt())
    }

    override fun onClose() { Minecraft.getInstance().gui.setScreen(parentScreen) }
}
