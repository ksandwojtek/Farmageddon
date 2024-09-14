package com.ksndq.farmageddon.utils

import com.ksndq.farmageddon.Farmageddon
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.inventory.ItemStack

object EnchantmentUtils {

    private val enchantmentRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT)



    fun isEnchantedWith(item: ItemStack, enchant: String): Boolean {
        val enchantKey = NamespacedKey("minecraft", enchant)
        val enchantment = enchantmentRegistry[enchantKey] ?: return false
        return item.containsEnchantment(enchantment)
    }

    fun scheduleBlockUpdate(block: Block, type: Material, data: BlockData) {
        Bukkit.getScheduler().runTaskLater(Farmageddon.getInstance(), Runnable {
            if(block.world.getBlockAt(block.location).type != Material.AIR) return@Runnable
            block.type = type
            block.blockData = data
        }, 3)
    }

    fun scheduleBlockChange(block: Block, type: Material) {
        Bukkit.getScheduler().runTaskLater(Farmageddon.getInstance(), Runnable {
            if(block.world.getBlockAt(block.location).type != Material.AIR) return@Runnable
            block.type = type
        }, 3)
    }
}