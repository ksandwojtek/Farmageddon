package com.ksndq.farmageddon.inventories

import com.ksndq.farmageddon.Farmageddon
import com.ksndq.farmageddon.utils.PlayerUtils.getPlayerNormalizedRotation
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class SprinklerInventory(sprinklerLocation: Location, player: Player) : InventoryHolder {
    private var inventory: Inventory = Farmageddon.getInstance().server.createInventory(this, 5 * 9)

    data class MaterialInfo(val material: Material, val color: Int)

    private val materialMap = mapOf(
        Material.WHEAT to MaterialInfo(Material.WHEAT, 0xd4b55f),
        Material.CARROTS to MaterialInfo(Material.CARROT, 0xffb33e),
        Material.BEETROOTS to MaterialInfo(Material.BEETROOT, 0xb4474b),
        Material.NETHER_WART to MaterialInfo(Material.NETHER_WART, 0x821e25),
        Material.POTATOES to MaterialInfo(Material.POTATO, 0xe6b861),
        Material.SWEET_BERRY_BUSH to MaterialInfo(Material.SWEET_BERRIES, 0x9e0200),
        Material.PITCHER_CROP to MaterialInfo(Material.PITCHER_PLANT, 0xb395f7),
        Material.TORCHFLOWER_CROP to MaterialInfo(Material.TORCHFLOWER, 0xffe850),
        Material.TORCHFLOWER to MaterialInfo(Material.TORCHFLOWER, 0xffe850)
    )

    private val cropSlots = listOf(
        2, 3, 4, 5, 6, 11, 12, 13, 14, 15, 20, 21, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42
    )

    init {
        setInventoryItems(getPlayerNormalizedRotation(player), sprinklerLocation)
    }

    private fun setInventoryItems(playerRotation: String, sprinklerLocation: Location) {
        val (xRange, zRange) = when (playerRotation) {
            "NORTH" -> -2..2 to -2..2
            "SOUTH" -> 2 downTo -2 to (2 downTo -2)
            "WEST" -> 2 downTo -2 to -2..2
            "EAST" -> -2..2 to (2 downTo -2)
            else -> -2..2 to (2 downTo -2)
        }

        var index = 0
        for (z in zRange) {
            for (x in xRange) {
                if(z == 0 && x == 0) continue
                val (adjustedX, adjustedZ) = if (playerRotation == "WEST" || playerRotation == "EAST") z to x else x to z
                val newBlock = sprinklerLocation.world.getBlockAt(
                    sprinklerLocation.clone().add(adjustedX.toDouble(), 0.0, adjustedZ.toDouble())
                )
                val blockType = newBlock.type
                val info = materialMap[blockType]

                Farmageddon.getInstance().logger.info((newBlock.blockData is Ageable).toString())

                val item = if (info != null) {
                    if (newBlock.blockData is Ageable) {
                        val ageableBlock = newBlock.blockData as Ageable
                        val growthProgress = when (blockType) {
                            Material.TORCHFLOWER_CROP -> (ageableBlock.age.toFloat() / (ageableBlock.maximumAge.toFloat() + 1.0f))
                            else -> ageableBlock.age.toFloat() / ageableBlock.maximumAge.toFloat()
                        }
                        createColoredItem(info.material, info.color, growthProgress = growthProgress)
                    } else {
                        createColoredItem(info.material, info.color, growthProgress = 1.0f)
                    }
                } else {
                    createColoredItem(Material.BARRIER, 0xFF0000, "Not a crop")
                }


                inventory.setItem(cropSlots.getOrNull(index) ?: return, item)
                index++
            }
        }
        val sprinklerItem = ItemStack(Material.DISPENSER).apply {
            itemMeta = itemMeta?.apply {
                addItemFlags(ItemFlag.HIDE_ENCHANTS)
                displayName(Component.text("Sprinkler").color(TextColor.color(0x5a83f3)))
                addEnchant(Enchantment.UNBREAKING, 0, true)
            }
        }
        inventory.setItem(22, sprinklerItem)
    }

    private fun createColoredItem(
        material: Material,
        color: Int,
        customName: String? = null,
        growthProgress: Float? = null
    ): ItemStack {
        return ItemStack(material).apply {
            itemMeta = itemMeta?.apply {
                displayName(
                    (customName ?: material.itemTranslationKey)?.let {
                        Component.translatable(it)
                            .color(TextColor.color(color))
                    }
                )

                if (growthProgress != null) {
                    val progressColor = when {
                        growthProgress <= 0.27 -> 0xBE0000
                        growthProgress <= 0.50 -> 0xE48900
                        growthProgress <= 0.75 -> 0xF7EA00
                        growthProgress < 1.0 -> 0x9EDE73
                        else -> 0x006400
                    }

                    lore(
                        listOf(
                            Component.text("Growth progress: ").color(TextColor.color(0xF5F7F8))
                                .append(
                                    Component.text(String.format("%.0f%%", growthProgress * 100))
                                        .color(TextColor.color(progressColor))
                                )
                        )
                    )
                }
            }
        }
    }

    override fun getInventory(): Inventory {
        return inventory
    }
}
