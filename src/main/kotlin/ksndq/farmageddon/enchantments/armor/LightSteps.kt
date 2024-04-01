package ksndq.farmageddon.enchantments.armor

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

class LightSteps(private val plugin: Plugin) : Listener {
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if(event.action != Action.PHYSICAL) return
        val block = event.clickedBlock ?: return
        if(block.type != Material.FARMLAND) return
        val helmet = event.player.inventory.boots ?: return
        if(!helmet.hasItemMeta()) return
        if(!helmet.itemMeta.persistentDataContainer.has(NamespacedKey(plugin, "lightsteps"))) return
        event.isCancelled = true
    }
}