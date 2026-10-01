package net.ccbluex.liquidbounce.gametest

import com.mojang.authlib.GameProfile
import net.ccbluex.liquidbounce.config.ConfigSystem
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.event.events.AttackEntityEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.events.TransferOrigin
import net.ccbluex.liquidbounce.features.blink.BlinkManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.modules.combat.backtrack.ModuleBacktrack
import net.ccbluex.liquidbounce.integration.backend.BrowserBackendManager
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext
import net.minecraft.client.Minecraft
import net.minecraft.client.player.RemotePlayer
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.GamePacketTypes
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class BacktrackOriginalGameTestTodoAi : FabricClientGameTest {
    private val handled = AtomicInteger()
    private inner class Probe : Packet<ClientGamePacketListener> {
        override fun type() = GamePacketTypes.CLIENTBOUND_SET_TIME
        override fun handle(listener: ClientGamePacketListener) { handled.incrementAndGet() }
    }

    override fun runTest(context: ClientGameTestContext) {
        context.waitForClient()
        context.onClient {
            check(ModuleManager.map { it.name } == listOf("Backtrack"))
            check(BrowserBackendManager.backend == null)
        }
        BacktrackConfigGameTestTodoAi().runTest(context)
        context.worldBuilder().create().use {
            context.waitFor { client -> client.player != null && client.player!!.tickCount > 20 }
            prepare(context)
            context.waitFor { handled.get() == 1 }
            checkModes(context)
            context.onClient {
                val packet = EventManager.callEvent(PacketEvent(TransferOrigin.INCOMING, Probe()))
                check(packet.isCancelled)
                ModuleBacktrack.enabled = false
                check(handled.get() == 2)
                check(BlinkManager.packetQueue.isEmpty())
            }
        }
        context.onClient { check(BlinkManager.packetQueue.isEmpty()) }
    }

    private fun prepare(context: ClientGameTestContext) = context.onClient {
        val client = Minecraft.getInstance()
        val player = client.player!!
        val target = RemotePlayer(client.level!!, GameProfile(UUID.randomUUID(), "BacktrackTest"))
        target.setId(123456789)
        target.setPos(player.x, player.y, player.z + 2)
        target.positionCodec.setBase(target.position())
        client.level!!.addEntity(target)
        ModuleBacktrack.settings.getValue("Chance").setByString("100")
        ModuleBacktrack.settings.getValue("LastAttackTimeToWork").setByString("5000")
        ModuleBacktrack.enabled = true
        EventManager.callEvent(AttackEntityEvent(target))
        check(ModuleBacktrack.shouldCancelPackets())
        val packet = EventManager.callEvent(PacketEvent(TransferOrigin.INCOMING, Probe()))
        check(packet.isCancelled && handled.get() == 0)
    }

    private fun checkModes(context: ClientGameTestContext) {
        for (mode in listOf("Box", "Wireframe", "Model", "None")) {
            context.onClient {
                ModuleBacktrack.settings.getValue("Esp").setByString(mode)
                val paths = ConfigSystem.valueKeySequence("").toList()
                check(paths.any { it.contains("backtrack", ignoreCase = true) })
            }
            context.waitTicks(2)
            if (mode != "None") context.takeScreenshot("backtrack-$mode")
        }
    }
}
