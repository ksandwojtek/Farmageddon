package ksndq.farmageddon.enchantments

import ksndq.farmageddon.Farmageddon.Companion.maxLevels
import ksndq.farmageddon.commands.EnchantCommandExecutor
import ksndq.farmageddon.utils.NumberUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class AnvilHandler(private val plugin: Plugin) : Listener {
    @EventHandler
    fun onAnvil(e: PrepareAnvilEvent) {
        val items = e.inventory.contents
        if (items[0] == null || items[1] == null || items[0]!!.type != items[1]!!.type) return

        val meta1 = items[0]!!.itemMeta
        val meta2 = items[1]!!.itemMeta
        val result = e.result ?: items[0]!!.clone()
        val resultMeta = result.itemMeta

        for (enchant in EnchantList.entries) {
            val enchantString: String = enchant.toString().toLowerCase()
            val key = NamespacedKey(plugin, enchantString)
            val level1 = meta1?.persistentDataContainer?.get(key, PersistentDataType.INTEGER) ?: 0
            val level2 = meta2?.persistentDataContainer?.get(key, PersistentDataType.INTEGER) ?: 0
            val resultLevel = maxLevels[enchant]?.let { Math.min(level1 + level2, it) }

            resultLevel?.let {
                if (it > 0) {
                    resultMeta?.persistentDataContainer?.set(key, PersistentDataType.INTEGER, it)
                    val lore = resultMeta?.lore ?: ArrayList()
                    lore.removeIf { string -> string.contains(EnchantCommandExecutor.normalizeName(enchantString)) }
                    lore.add("${ChatColor.GRAY} ${EnchantCommandExecutor.normalizeName(enchantString)} ${NumberUtils.toRoman(it)}")
                    resultMeta.lore = lore
                }
            }
        }
        result.itemMeta = resultMeta
        e.result = result
    }
}
