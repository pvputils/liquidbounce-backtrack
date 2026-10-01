package net.ccbluex.liquidbounce.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

class BacktrackModMenuTodoAi : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory { parent -> BacktrackConfigScreenTodoAi(parent) }
}
