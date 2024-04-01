package ksndq.farmageddon

import ksndq.farmageddon.blocks.Sprinkler
import ksndq.farmageddon.commands.EnchantCommandExecutor
import ksndq.farmageddon.enchantments.AnvilHandler
import ksndq.farmageddon.enchantments.EnchantList
import ksndq.farmageddon.enchantments.armor.LightSteps
import ksndq.farmageddon.enchantments.tools.Delicate
import ksndq.farmageddon.enchantments.tools.Replenish
import ksndq.farmageddon.items.FarmersGoggles
import ksndq.farmageddon.listeners.blocks.SprinklerListener
import ksndq.farmageddon.listeners.items.GogglesListener
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.function.Consumer


class Farmageddon : JavaPlugin() {
    override fun onEnable() {
        plugin = this

        itemIDKey = NamespacedKey(this, "item-id-key")
        gogglesStandKey = NamespacedKey(this, "goggles-block-key")
        itemUUIDKey = NamespacedKey(this, "item-uuid")

        FarmersGoggles.addRecipe()
        Sprinkler.addRecipe()

        maxLevels[EnchantList.LIGHTSTEPS] = 1
        maxLevels[EnchantList.REPLENISH] = 1
        maxLevels[EnchantList.DELICATE] = 1

        getCommand("cenchant")!!.setExecutor(EnchantCommandExecutor)
        registerListeners(GogglesListener(this), AnvilHandler(this), SprinklerListener(this))
        registerListeners(LightSteps(this), Replenish(this), Delicate(this))
    }

    override fun onDisable() {
        FarmersGoggles.removeRecipe()
        Sprinkler.removeRecipe()
    }

    private fun registerListeners(vararg listeners: Listener) {
        listOf(*listeners).forEach(Consumer { i: Listener? ->
            Bukkit.getPluginManager().registerEvents(
                i!!, this
            )
        })
    }

    companion object {
        lateinit var plugin: Plugin
        var maxLevels: MutableMap<EnchantList, Int> = EnumMap(EnchantList::class.java)
        var gogglesStandKey: NamespacedKey? = null
        var itemIDKey: NamespacedKey? = null
        var itemUUIDKey: NamespacedKey? = null
        val plantableCrops = arrayOf(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.NETHER_WART, Material.COCOA)
    }
}
