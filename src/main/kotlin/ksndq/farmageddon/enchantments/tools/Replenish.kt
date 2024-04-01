package ksndq.farmageddon.enchantments.tools

import ksndq.farmageddon.utils.ItemUtils
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable


class Replenish(private val plugin: Plugin) : Listener {

    private val utils: ItemUtils = ItemUtils()
    private val cropDrops: Map<Material, Material> = mapOf(
        Material.WHEAT to Material.WHEAT_SEEDS,
        Material.CARROTS to Material.CARROT,
        Material.POTATOES to Material.POTATO,
        Material.BEETROOTS to Material.BEETROOT_SEEDS,
        Material.NETHER_WART to Material.NETHER_WART,
        Material.CACTUS to Material.CACTUS,
        Material.SUGAR_CANE to Material.SUGAR_CANE,
        Material.COCOA to Material.COCOA_BEANS
    )

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (player.gameMode != GameMode.SURVIVAL) return
        if (item.type.isAir) return
        if (!item.hasItemMeta()) return
        if (!(utils.isAxe(item) || utils.isHoe(item))) return
        if (!item.itemMeta.persistentDataContainer.has(NamespacedKey(plugin, "replenish"))) return
        val finalMaterial: Material = event.block.type
        val material = cropDrops[finalMaterial] ?: return
        if (material == Material.CACTUS || material == Material.SUGAR_CANE) {
            val block = event.block.getRelative(0, -1, 0)
            if (block.type == material) return
            if (event.block.getRelative(0, 1, 0).type != material && event.block.getRelative(0, 2, 0).type != material
            ) return
            object : BukkitRunnable() {
                override fun run() {
                    event.block.setType(finalMaterial, true)
                }
            }.runTaskLater(plugin, 3)
            event.block.type = Material.AIR
            event.isCancelled = true
        } else {
            event.isDropItems = false
            val ageable = event.block.blockData as Ageable
            if (ageable.age != ageable.maximumAge) return
            ageable.age = 0
            object : BukkitRunnable() {
                override fun run() {
                    event.block.setType(finalMaterial, true)
                    event.block.blockData = ageable
                }
            }.runTaskLater(plugin, 2)

            val drops = event.block.drops.toMutableList()
            event.block.drops.clear()
            val seedStack = drops.firstOrNull { it.type == material }
            if (seedStack != null) {
                seedStack.amount = (seedStack.amount - 1).coerceAtLeast(0)
                drops.remove(seedStack)
                drops.add(seedStack)
            }
            for (drop in drops) {
                event.block.world.dropItemNaturally(event.block.location, drop)
            }

        }
    }
}