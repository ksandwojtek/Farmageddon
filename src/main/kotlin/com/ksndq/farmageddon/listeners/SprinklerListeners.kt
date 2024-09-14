package com.ksndq.farmageddon.listeners

import com.ksndq.farmageddon.inventories.SprinklerInventory
import com.ksndq.farmageddon.items.Sprinkler.WATER_LEVEL
import com.ksndq.farmageddon.utils.ItemUtils
import com.ksndq.farmageddon.utils.PlayerUtils.getPlayerNormalizedRotation
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Dropper
import org.bukkit.block.data.Directional
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType

object SprinklerListeners: Listener {
    @EventHandler
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        if (event.isCancelled) return

        val block = event.blockPlaced
        if (block.type != Material.DISPENSER) return

        val item = event.itemInHand
        if (!ItemUtils.hasID(item, "SPRINKLER")) return

        val state = block.state.apply {
            if (this is Dropper) {
                persistentDataContainer[WATER_LEVEL, PersistentDataType.INTEGER] = 0
                update()
            }
        }

        (block.blockData as? Directional)?.let { directional ->
            directional.facing = BlockFace.UP
            state.blockData = directional
            state.update(true, true)
        }
    }

    @EventHandler
    fun onInventoryOpen(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock ?: return
        if (block.type != Material.DISPENSER) return
        val player = event.player
        event.apply {
            isCancelled = true
            player.openInventory(SprinklerInventory(block.location.add(0.0, 1.0, 0.0), player).inventory)
        }
    }
}