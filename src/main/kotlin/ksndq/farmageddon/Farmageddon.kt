package ksndq.farmageddon

import ksndq.farmageddon.items.FarmersGoggles
import ksndq.farmageddon.listeners.GogglesListener
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Consumer

class Farmageddon : JavaPlugin() {
    override fun onEnable() {
        registerListeners(GogglesListener(this))
        gogglesStandKey = NamespacedKey(this, "goggles-block-key")
        itemIDKey = NamespacedKey(this, "item-id-key")
        farmersGogglesItemKey = NamespacedKey(this, "farmers_goggles")

        FarmersGoggles.addRecipe()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun registerListeners(vararg listeners: Listener) {
        listOf(*listeners).forEach(Consumer { i: Listener? ->
            Bukkit.getPluginManager().registerEvents(
                i!!, this
            )
        })
    }

    companion object {
        var gogglesStandKey: NamespacedKey? = null
        var itemIDKey: NamespacedKey? = null
        var farmersGogglesItemKey: NamespacedKey? = null
    }
}
