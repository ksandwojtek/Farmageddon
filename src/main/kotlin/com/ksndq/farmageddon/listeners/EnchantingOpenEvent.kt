package com.ksndq.farmageddon.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent

object EnchantingOpenEvent : Listener {

    @EventHandler
    fun onEnchantingOpen(event: InventoryOpenEvent) {
        event.player.inventory.itemInMainHand.enchantments.forEach { (enchantment) ->
            event.player.sendMessage(
                "Enchantment: IsDiscoverable: ${enchantment.isDiscoverable}, isCurse ${enchantment.isCursed}, isTreasure ${enchantment.isTreasure}, isTradeable ${enchantment.isTradeable}"
            )
        }
    }
}