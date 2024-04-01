package ksndq.farmageddon.utils

import ksndq.farmageddon.Farmageddon
import ksndq.farmageddon.Farmageddon.Companion.plugin
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


class ItemUtils {

    private val enchantment: Enchantment? = null
    fun isHoe(item: ItemStack): Boolean {
        return (item.type == Material.WOODEN_HOE ||
                item.type == Material.STONE_HOE ||
                item.type == Material.IRON_HOE ||
                item.type == Material.GOLDEN_HOE ||
                item.type == Material.DIAMOND_HOE ||
                item.type == Material.NETHERITE_HOE)
    }

    fun isAxe(item: ItemStack): Boolean {
        return (item.type == Material.WOODEN_AXE ||
                item.type == Material.STONE_AXE ||
                item.type == Material.IRON_AXE ||
                item.type == Material.GOLDEN_AXE ||
                item.type == Material.DIAMOND_AXE ||
                item.type == Material.NETHERITE_AXE)
    }

    fun isSword(item: ItemStack): Boolean {
        return (item.type == Material.WOODEN_SWORD ||
                item.type == Material.STONE_SWORD ||
                item.type == Material.IRON_SWORD ||
                item.type == Material.GOLDEN_SWORD ||
                item.type == Material.DIAMOND_SWORD ||
                item.type == Material.NETHERITE_SWORD)
    }

    fun hasEnchant(item: ItemStack, name: String): Boolean {
        if (!item.hasItemMeta()) return false
        val meta = item.itemMeta
        val container = meta.persistentDataContainer
        return container.has(NamespacedKey(plugin, name))
    }

    fun hasID(item: ItemStack?, name: String): Boolean {
        if(item == null) return false
        if (!item.hasItemMeta()) return false
        val meta = item.itemMeta
        val container = meta.persistentDataContainer
        return container[Farmageddon.itemIDKey!!, PersistentDataType.STRING] == name
    }
}