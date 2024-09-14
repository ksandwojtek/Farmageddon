package com.ksndq.farmageddon.enchantments.impl.tool

import com.ksndq.farmageddon.Farmageddon
import com.ksndq.farmageddon.utils.EnchantmentUtils.isEnchantedWith
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object DelicateEnchant : Listener {

    private val cropList = listOf(
        Material.WHEAT,
        Material.CARROTS,
        Material.POTATOES,
        Material.BEETROOTS,
        Material.NETHER_WART,
        Material.COCOA,
        Material.PUMPKIN_STEM,
        Material.MELON_STEM,
        Material.TORCHFLOWER_CROP
    )

    private val blockList = listOf(
        Material.CACTUS,
        Material.SUGAR_CANE,
        Material.BAMBOO_SAPLING,
        Material.KELP_PLANT,
        Material.KELP
    )

    @EventHandler
    fun onCropBreak(event: BlockBreakEvent) {
        if (!Farmageddon.delicateEnabled) return

        val player = event.player
        if(event.player.gameMode != GameMode.SURVIVAL) return
        val item = player.inventory.itemInMainHand
        if (!isEnchantedWith(item, "delicate")) return

        val block = event.block
        val blockType = block.type
        val blockBelow = block.getRelative(0, -1, 0)

        if (blockBelow.type == blockType) return

        if (blockType in cropList) {
            (block.blockData as? Ageable)?.let { ageable ->
                if (ageable.age != ageable.maximumAge) {
                    event.isCancelled = true
                }
            }
        } else if (blockType in blockList) {
            if (blockType != Material.KELP || block.getRelative(0, 1, 0).type != Material.KELP) {
                event.isCancelled = true
            }
        }
    }
}
