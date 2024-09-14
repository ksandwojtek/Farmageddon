package com.ksndq.farmageddon.enchantments.impl.tool

import com.ksndq.farmageddon.Farmageddon
import com.ksndq.farmageddon.utils.EnchantmentUtils.isEnchantedWith
import com.ksndq.farmageddon.utils.EnchantmentUtils.scheduleBlockChange
import com.ksndq.farmageddon.utils.EnchantmentUtils.scheduleBlockUpdate
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

object ReplenishEnchant : Listener {

    private val cropList = setOf(
        Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS,
        Material.NETHER_WART, Material.COCOA
    )

    private val blockList = setOf(Material.CACTUS, Material.SUGAR_CANE)

    private val seedList = setOf(
        Material.WHEAT_SEEDS, Material.CARROT, Material.POTATO, Material.BEETROOT_SEEDS,
        Material.NETHER_WART, Material.COCOA_BEANS, Material.CACTUS, Material.SUGAR_CANE,
        Material.BAMBOO, Material.KELP, Material.TORCHFLOWER_SEEDS
    )

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        if (player.gameMode != GameMode.SURVIVAL || !Farmageddon.replenishEnabled) return

        val item = player.inventory.itemInMainHand
        if (!isEnchantedWith(item, "replenish")) return

        val block = event.block
        val blockType = block.type
        val blockData = block.blockData
        val floorBlock = block.getRelative(0, -1, 0)

        if (floorBlock.type == Material.AIR || floorBlock.type == blockType) return

        when (blockType) {
            in cropList -> handleCropReplenish(block, blockData, player)
            in blockList -> handleVerticalReplenish(block, blockType, 2, null, player)
            Material.TORCHFLOWER -> handleTorchflowerReplenish(block, player)
            Material.BAMBOO -> handleVerticalReplenish(block, Material.BAMBOO, 4, Material.BAMBOO_SAPLING, player)
            else -> return
        }
    }

    private fun handleCropReplenish(block: Block, blockData: BlockData, player: Player) {
        val ageable = blockData as? Ageable ?: return
        if (ageable.age != ageable.maximumAge) return

        ageable.age = 0
        if (consumeSeed(player)) {
            scheduleBlockUpdate(block, block.type, ageable)
        }
    }

    private fun handleVerticalReplenish(block: Block, blockType: Material, height: Int, saplingType: Material?, player: Player) {
        if ((1..height).all { block.getRelative(0, it, 0).type == blockType } && consumeSeed(player)) {
            scheduleBlockChange(block, saplingType ?: blockType)
        }
    }

    private fun handleTorchflowerReplenish(block: Block, player: Player) {
        if (consumeSeed(player, Material.TORCHFLOWER_SEEDS)) {
            scheduleBlockChange(block, Material.TORCHFLOWER_SEEDS)
        }
    }

    private fun consumeSeed(player: Player, replacementType: Material? = null): Boolean {
        val inventory = player.inventory
        for (inventoryItem in inventory.contents) {
            if (inventoryItem != null && seedList.contains(inventoryItem.type)) {
                inventoryItem.amount--
                return true
            }
        }
        return false
    }
}
