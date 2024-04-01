package ksndq.farmageddon.enchantments.tools

import ksndq.farmageddon.Farmageddon.Companion.plantableCrops
import ksndq.farmageddon.utils.ItemUtils
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.Plugin

class Delicate(private val plugin: Plugin): Listener {

    private val utils: ItemUtils = ItemUtils()
    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return
        val player = event.player
        val item = player.inventory.itemInMainHand
        if (player.gameMode != GameMode.SURVIVAL) return
        if (item.type.isAir) return
        if (!item.hasItemMeta()) return
        if (!(utils.isAxe(item) || utils.isHoe(item))) return
        if (!item.itemMeta.persistentDataContainer.has(NamespacedKey(plugin, "delicate"))) return
        if(!plantableCrops.contains(event.block.type)) return
        val ageable = event.block.blockData as Ageable
        if(ageable.age == ageable.maximumAge) return
        event.isCancelled = true
    }
}