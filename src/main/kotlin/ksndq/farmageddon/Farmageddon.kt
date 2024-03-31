package ksndq.farmageddon

import ksndq.farmageddon.commands.EnchantCommandExecutor
import ksndq.farmageddon.enchantments.AnvilHandler
import ksndq.farmageddon.enchantments.EnchantList
import ksndq.farmageddon.enchantments.armor.LightSteps
import ksndq.farmageddon.items.FarmersGoggles
import ksndq.farmageddon.listeners.GogglesListener
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer


class Farmageddon : JavaPlugin() {
    override fun onEnable() {
        itemIDKey = NamespacedKey(this, "item-id-key")
        gogglesStandKey = NamespacedKey(this, "goggles-block-key")
        farmersGogglesItemKey = NamespacedKey(this, "farmers_goggles")

        FarmersGoggles.addRecipe()

        maxLevels[EnchantList.LIGHTSTEPS] = 1

        getCommand("cenchant")!!.setExecutor(EnchantCommandExecutor)
        registerListeners(GogglesListener(this), AnvilHandler(this), LightSteps(this))
    }

    override fun onDisable() {
        FarmersGoggles.removeRecipe()
    }

    private fun registerListeners(vararg listeners: Listener) {
        listOf(*listeners).forEach(Consumer { i: Listener? ->
            Bukkit.getPluginManager().registerEvents(
                i!!, this
            )
        })
    }

    companion object {
        var maxLevels: MutableMap<EnchantList, Int> = EnumMap(EnchantList::class.java)
        var gogglesStandKey: NamespacedKey? = null
        var itemIDKey: NamespacedKey? = null
        var farmersGogglesItemKey: NamespacedKey? = null
    }
}
