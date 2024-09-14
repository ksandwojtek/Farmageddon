package com.ksndq.farmageddon.utils

import com.ksndq.farmageddon.Farmageddon
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemUtils {

    fun hasID(item: ItemStack?, name: String): Boolean {
        if(item == null) return false
        if (!item.hasItemMeta()) return false
        val meta = item.itemMeta
        val container = meta.persistentDataContainer
        return container[Farmageddon.itemIDKey, PersistentDataType.STRING]?.contains(name) ?: false
    }
}