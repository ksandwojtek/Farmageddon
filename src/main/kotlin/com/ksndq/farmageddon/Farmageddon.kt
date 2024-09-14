package com.ksndq.farmageddon

import com.ksndq.farmageddon.enchantments.EnchantManager_v1_21_R1
import com.ksndq.farmageddon.enchantments.impl.tool.DelicateEnchant
import com.ksndq.farmageddon.enchantments.impl.tool.ReplenishEnchant
import com.ksndq.farmageddon.listeners.EnchantingOpenEvent
import com.ksndq.farmageddon.items.Sprinkler
import com.ksndq.farmageddon.listeners.SprinklerListeners
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Consumer

class Farmageddon : JavaPlugin() {

    companion object {
        private var plugin: Plugin? = null
        var delicateEnabled: Boolean = true
        var replenishEnabled: Boolean = true

        lateinit var itemIDKey: NamespacedKey
        lateinit var itemUUIDKey: NamespacedKey

            @JvmStatic
        fun getInstance(): Plugin {
            return plugin ?: throw IllegalStateException("Plugin instance is not initialized.")
        }
    }

    override fun onLoad() {
    }

    override fun onEnable() {
        plugin = this

        saveResource("enchantments.yml",false)

        delicateEnabled = config.getBoolean("enchantments.delicate.enabled")
        replenishEnabled = config.getBoolean("enchantments.replenish.enabled")

        itemIDKey = NamespacedKey(this, "item-id")
        itemUUIDKey = NamespacedKey(this, "item-uuid")

        Sprinkler.addRecipe()

        if (Bukkit.getWorlds().isEmpty()) {
            val serverVersion = Bukkit.getVersion()
            if (serverVersion.contains("1.21")) {
                EnchantManager_v1_21_R1.registerEnchantments()
            }
        }

        registerListeners(EnchantingOpenEvent)

        registerListeners(ReplenishEnchant, DelicateEnchant)

        registerListeners(SprinklerListeners)

        saveDefaultConfig()
    }

    override fun onDisable() {
        Sprinkler.removeRecipe()
    }

    private fun registerListeners(vararg listeners: Listener) {
        listOf(*listeners).forEach(Consumer { i: Listener? ->
            Bukkit.getPluginManager().registerEvents(
                i!!, this
            )
        })
    }
}
