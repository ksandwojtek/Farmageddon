package ksndq.farmageddon.listeners.blocks

import com.jeff_media.customblockdata.CustomBlockData
import ksndq.farmageddon.Farmageddon.Companion.itemIDKey
import ksndq.farmageddon.Farmageddon.Companion.itemUUIDKey
import ksndq.farmageddon.utils.ItemUtils
import ksndq.farmageddon.utils.UUIDDataType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class SprinklerListener(private val plugin: JavaPlugin) : Listener {
    private val utils: ItemUtils = ItemUtils()
    private val WATER_LEVEL = NamespacedKey(plugin, "water-level")

    //Asign a random UUID on crafting to make it unstackable
    @EventHandler
    fun onCraftingResult(event: PrepareItemCraftEvent) {
        if (event.inventory.type != InventoryType.WORKBENCH) return
        val recipe = event.recipe ?: return
        if (!recipe.result.hasItemMeta()) return
        val result = recipe.result
        val resultMeta = result.itemMeta
        if (resultMeta.persistentDataContainer[itemIDKey!!, PersistentDataType.STRING] != "SPRINKLER") return
        val resultCopy = result.clone()
        val copyMeta = resultCopy.itemMeta
        copyMeta.persistentDataContainer[itemUUIDKey!!, UUIDDataType()] = UUID.randomUUID()
        resultCopy.itemMeta = copyMeta
        event.inventory.result = resultCopy
    }

    @EventHandler
    fun onBlockPlaceEvent(event: BlockPlaceEvent) {
        val block = event.blockPlaced
        if (block.type != Material.DISPENSER) return
        val item = event.itemInHand
        if (!utils.hasID(item, "SPRINKLER")) return
        val customBlockData = CustomBlockData(block, plugin)
        customBlockData[WATER_LEVEL, PersistentDataType.INTEGER] = getWaterLevel(item)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onWaterClickEvent(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock ?: return
        if (block.type != Material.DISPENSER) return
        if (!event.hasItem()) return
        if (event.item?.type != Material.WATER_BUCKET) return
        val player = event.player
        if (player.isSneaking) return
        val customBlockData = CustomBlockData(block, plugin)
        val waterLevel = customBlockData[WATER_LEVEL, PersistentDataType.INTEGER] ?: return
        event.isCancelled = true
        if (waterLevel == 100) {
            player.sendMessage("§cThe sprinkler is full of water!")
            return
        }
        if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
            player.inventory.itemInMainHand.type = Material.BUCKET
        }
        customBlockData[WATER_LEVEL, PersistentDataType.INTEGER] = waterLevel.coerceAtMost(100) + 20
        Bukkit.getLogger().info(customBlockData[WATER_LEVEL, PersistentDataType.INTEGER].toString())
    }

    @EventHandler
    fun onInventoryOpen(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock ?: return
        if (block.type != Material.DISPENSER) return
        if (event.item?.type == Material.WATER_BUCKET) return
        val player = event.player
        event.isCancelled = true
        player.openInventory(createInventory(block))
    }

    private fun getWaterLevel(item: ItemStack): Int {
        val itemMeta = item.itemMeta
        val container = itemMeta.persistentDataContainer
        val waterLevel = container[WATER_LEVEL, PersistentDataType.INTEGER] ?: return 0
        return waterLevel
    }

    private fun createInventory(block: Block): Inventory {
        val inv = Bukkit.getServer().createInventory(
            null, 45, Component.text().content("Sprinkler").color(
                TextColor.color(0x5a83f3)
            ).build()
        )
        val customBlockData = CustomBlockData(block, plugin)
        val waterLevel = customBlockData[WATER_LEVEL, PersistentDataType.INTEGER] ?: return inv
        val waterBar = mutableListOf(36, 27, 18, 9, 0)
        val emptyBar = waterBar.toMutableList()
        val fullName = Component.text().content("$waterLevel%").color(
            TextColor.color(0x5a83f3)
        ).build()
        val emptyName = Component.text().content("$waterLevel%").color(
            TextColor.color(0xAAAAAA)
        ).build()

        for (i in 0 until (waterLevel / 20).coerceAtMost(waterBar.size)) {
            val slot = waterBar[i]
            Bukkit.getLogger().info(slot.toString())
            inv.setItem(slot, createInventoryItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, fullName, null))
            emptyBar.remove(slot)
        }
        for (i in emptyBar) {
            inv.setItem(i, createInventoryItem(Material.GRAY_STAINED_GLASS_PANE, emptyName, null))
        }

        return inv
    }

    private fun createInventoryItem(material: Material, name: Component, lore: Component?): ItemStack {
        val item = ItemStack(material, 1)
        val itemMeta = item.itemMeta
        itemMeta.displayName(name)
        if (lore != null) {
            itemMeta.lore(listOf(lore))
        }
        item.itemMeta = itemMeta

        return item
    }
}