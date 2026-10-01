package net.ccbluex.liquidbounce.fabric

import net.ccbluex.liquidbounce.features.module.modules.combat.backtrack.ModuleBacktrack
import net.ccbluex.liquidbounce.features.module.modules.combat.aimbot.ModuleAutoBow
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleAutoClicker
import net.ccbluex.liquidbounce.features.module.modules.player.autobuff.ModuleAutoBuff
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleAutoWeapon
import net.ccbluex.liquidbounce.features.module.modules.combat.criticals.ModuleCriticals
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleHitbox
import net.ccbluex.liquidbounce.features.module.modules.combat.killaura.ModuleKillAura
import net.ccbluex.liquidbounce.features.module.modules.combat.velocity.ModuleVelocity
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleSwordBlock
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleKeepSprint
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleMaceKill
import net.ccbluex.liquidbounce.features.module.modules.combat.spearkill.ModuleSpearKill
import net.ccbluex.liquidbounce.features.module.modules.combat.ModuleNoMissCooldown
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModuleAntiReducedDebugInfo
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModuleExtendedFirework
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModuleResetVL
import net.ccbluex.liquidbounce.features.module.modules.exploit.disabler.ModuleDisabler
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModuleGhostHand
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModuleMultiActions
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModuleNoPitchLimit
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModulePingSpoof
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModulePlugins
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModulePortalMenu
import net.ccbluex.liquidbounce.features.module.modules.exploit.servercrasher.ModuleServerCrasher
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleTeleport
import net.ccbluex.liquidbounce.features.module.modules.exploit.ModuleYggdrasilSignatureFix
import net.ccbluex.liquidbounce.features.module.modules.`fun`.ModuleDankBobbing
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleAutoConfig
import net.ccbluex.liquidbounce.features.module.modules.misc.antibot.ModuleAntiBot
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleBetterTab
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleItemScroller
import net.ccbluex.liquidbounce.features.module.modules.misc.betterchat.ModuleBetterChat
import net.ccbluex.liquidbounce.features.module.modules.combat.elytratarget.ModuleElytraTarget
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleMiddleClickAction
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleInventoryTracker
import net.ccbluex.liquidbounce.features.module.modules.misc.nameprotect.ModuleNameProtect
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleAutoAccount
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleAntiStaff
import net.ccbluex.liquidbounce.features.module.modules.misc.ModulePacketLogger
import net.ccbluex.liquidbounce.features.module.modules.misc.debugrecorder.ModuleDebugRecorder
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleAntiCheatDetect
import net.ccbluex.liquidbounce.features.module.modules.misc.ModuleEasyPearl
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleAirJump
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleAntiBounce
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleAntiLevitation
import net.ccbluex.liquidbounce.features.module.modules.movement.autododge.ModuleAutoDodge
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleAvoidHazards
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleElytraRecast
import net.ccbluex.liquidbounce.features.module.modules.movement.fly.ModuleFly
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleFreeze
import net.ccbluex.liquidbounce.features.module.modules.movement.inventorymove.ModuleInventoryMove
import net.ccbluex.liquidbounce.features.module.modules.movement.liquidwalk.ModuleLiquidWalk
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleNoClip
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleNoJumpDelay
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleNoPose
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleNoPush
import net.ccbluex.liquidbounce.features.module.modules.movement.noslow.ModuleNoSlow
import net.ccbluex.liquidbounce.features.module.modules.movement.noweb.ModuleNoWeb
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleEntityControl
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleSafeWalk
import net.ccbluex.liquidbounce.features.module.modules.movement.speed.ModuleSpeed
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleSprint
import net.ccbluex.liquidbounce.features.module.modules.movement.ModuleVehicleControl
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleAntiExploit
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleAutoBreak
import net.ccbluex.liquidbounce.features.module.modules.player.offhand.ModuleOffhand
import net.ccbluex.liquidbounce.features.module.modules.player.autoshop.ModuleAutoShop
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleBlink
import net.ccbluex.liquidbounce.features.module.modules.player.cheststealer.ModuleChestStealer
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleEagle
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleFastUse
import net.ccbluex.liquidbounce.features.module.modules.player.invcleaner.ModuleInventoryCleaner
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleNoBlockInteract
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleNoEntityInteract
import net.ccbluex.liquidbounce.features.module.modules.player.nofall.ModuleNoFall
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleNoRotateSet
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleReach
import net.ccbluex.liquidbounce.features.module.modules.render.animations.ModuleAnimations
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleAntiBlind
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleBetterInventory
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleBlockOutline
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleClickGui
import net.ccbluex.liquidbounce.features.module.modules.render.esp.ModuleESP
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleLogoffSpot
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleFreeCam
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleSmoothCamera
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleFreeLook
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleHud
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleItemESP
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleMobOwners
import net.ccbluex.liquidbounce.features.module.modules.render.murdermystery.ModuleMurderMystery
import net.ccbluex.liquidbounce.features.module.modules.render.hitfx.ModuleHitFX
import net.ccbluex.liquidbounce.features.module.modules.render.nametags.ModuleNametags
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleCombineMobs
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleAspect
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleChams
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleNoBob
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleNoFov
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleNoHurtCam
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleNoSwing
import net.ccbluex.liquidbounce.features.module.modules.render.customambience.ModuleCustomAmbience
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleQuickPerspectiveSwap
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleRotations
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleSilentHotbar
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleStorageESP
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleTNTTimer
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleTracers
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleTrueSight
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleXRay
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleDebug
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleZoom
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleItemChams
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleCrystalView
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleSkinChanger
import net.ccbluex.liquidbounce.features.module.modules.render.crosshair.ModuleCrosshair
import net.ccbluex.liquidbounce.features.module.modules.world.ModuleAutoDisable
import net.ccbluex.liquidbounce.features.module.modules.world.ModuleAutoTool
import net.ccbluex.liquidbounce.features.module.modules.combat.crystalaura.ModuleCrystalAura
import net.ccbluex.liquidbounce.features.module.modules.world.ModuleNoSlowBreak
import net.ccbluex.liquidbounce.features.module.modules.world.ModuleLiquidPlace
import net.ccbluex.liquidbounce.features.module.modules.world.scaffold.ModuleScaffold
import net.ccbluex.liquidbounce.features.module.modules.world.packetmine.ModulePacketMine
import net.ccbluex.liquidbounce.features.module.modules.world.ModuleNoInterpolation

/** Preserve upstream singleton initialization order; only Backtrack remains registered and active. */
object BacktrackDependenciesTodoAi {
    @Suppress("LongMethod")
    fun initialize() {
        val dependencies = arrayOf(
            ModuleAutoBow,
            ModuleAutoClicker,
            ModuleAutoBuff,
            ModuleAutoWeapon,
            ModuleCriticals,
            ModuleHitbox,
            ModuleKillAura,
            ModuleVelocity,
            ModuleBacktrack,
            ModuleSwordBlock,
            ModuleKeepSprint,
            ModuleMaceKill,
            ModuleSpearKill,
            ModuleNoMissCooldown,
            ModuleAntiReducedDebugInfo,
            ModuleExtendedFirework,
            ModuleResetVL,
            ModuleDisabler,
            ModuleGhostHand,
            ModuleMultiActions,
            ModuleNoPitchLimit,
            ModulePingSpoof,
            ModulePlugins,
            ModulePortalMenu,
            ModuleServerCrasher,
            ModuleTeleport,
            ModuleYggdrasilSignatureFix,
            ModuleDankBobbing,
            ModuleAutoConfig,
            ModuleAntiBot,
            ModuleBetterTab,
            ModuleItemScroller,
            ModuleBetterChat,
            ModuleElytraTarget,
            ModuleMiddleClickAction,
            ModuleInventoryTracker,
            ModuleNameProtect,
            ModuleAutoAccount,
            ModuleAntiStaff,
            ModulePacketLogger,
            ModuleDebugRecorder,
            ModuleAntiCheatDetect,
            ModuleEasyPearl,
            ModuleAirJump,
            ModuleAntiBounce,
            ModuleAntiLevitation,
            ModuleAutoDodge,
            ModuleAvoidHazards,
            ModuleElytraRecast,
            ModuleFly,
            ModuleFreeze,
            ModuleInventoryMove,
            ModuleLiquidWalk,
            ModuleNoClip,
            ModuleNoJumpDelay,
            ModuleNoPose,
            ModuleNoPush,
            ModuleNoSlow,
            ModuleNoWeb,
            ModuleEntityControl,
            ModuleSafeWalk,
            ModuleSpeed,
            ModuleSprint,
            ModuleVehicleControl,
            ModuleAntiExploit,
            ModuleAutoBreak,
            ModuleOffhand,
            ModuleAutoShop,
            ModuleBlink,
            ModuleChestStealer,
            ModuleEagle,
            ModuleFastUse,
            ModuleInventoryCleaner,
            ModuleNoBlockInteract,
            ModuleNoEntityInteract,
            ModuleNoFall,
            ModuleNoRotateSet,
            ModuleReach,
            ModuleAnimations,
            ModuleAntiBlind,
            ModuleBetterInventory,
            ModuleBlockOutline,
            ModuleClickGui,
            ModuleESP,
            ModuleLogoffSpot,
            ModuleFreeCam,
            ModuleSmoothCamera,
            ModuleFreeLook,
            ModuleHud,
            ModuleItemESP,
            ModuleMobOwners,
            ModuleMurderMystery,
            ModuleHitFX,
            ModuleNametags,
            ModuleCombineMobs,
            ModuleAspect,
            ModuleChams,
            ModuleNoBob,
            ModuleNoFov,
            ModuleNoHurtCam,
            ModuleNoSwing,
            ModuleCustomAmbience,
            ModuleQuickPerspectiveSwap,
            ModuleRotations,
            ModuleSilentHotbar,
            ModuleStorageESP,
            ModuleTNTTimer,
            ModuleTracers,
            ModuleTrueSight,
            ModuleXRay,
            ModuleDebug,
            ModuleZoom,
            ModuleItemChams,
            ModuleCrystalView,
            ModuleSkinChanger,
            ModuleCrosshair,
            ModuleAutoDisable,
            ModuleAutoTool,
            ModuleCrystalAura,
            ModuleNoSlowBreak,
            ModuleLiquidPlace,
            ModuleScaffold,
            ModulePacketMine,
            ModuleNoInterpolation,
        )
        dependencies.filterNot { it === ModuleBacktrack }.forEach { module ->
            module.enabled = false
            module.unregister()
        }
    }
}
