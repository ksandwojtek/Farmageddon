package com.ksndq.farmageddon.items

import com.ksndq.farmageddon.Farmageddon
import com.ksndq.farmageddon.Farmageddon.Companion.itemUUIDKey
import com.ksndq.farmageddon.datatypes.UUIDDataType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import java.util.*

object Sprinkler {
    private const val ITEM_ID = "SPRINKLER"
    private val ITEM_KEY = NamespacedKey(Farmageddon.getInstance(), "sprinkler")
    val WATER_LEVEL = NamespacedKey(Farmageddon.getInstance(), "water-level")

    fun getItem(level: Int = 0): ItemStack {
        return ItemStack(Material.DISPENSER).apply {
            itemMeta = itemMeta?.apply {
                addItemFlags(ItemFlag.HIDE_ENCHANTS)
                displayName(Component.text("Sprinkler").color(TextColor.color(0x5a83f3)))
                lore(createLore(level))
                persistentDataContainer[Farmageddon.itemIDKey, PersistentDataType.STRING] = ITEM_ID
                persistentDataContainer[itemUUIDKey, UUIDDataType()] = UUID.randomUUID()
                persistentDataContainer[WATER_LEVEL, PersistentDataType.INTEGER] = level
                addEnchant(Enchantment.UNBREAKING, 0, true)
            }
        }
    }

    private fun createLore(level: Int): List<Component> {
        val waterText = Component.text("Water Level:").color(TextColor.color(0xAAAAAA))
        val full = "■".repeat(level / 10)
        val missing = "■".repeat((100 - level) / 10)
        val waterBar = Component.text(full).color(TextColor.color(0x5a83f3))
            .append(Component.text(missing).color(TextColor.color(0x555555)))
            .append(
                Component.text(" $level%")
                    .color(if (level == 0) TextColor.color(0x555555) else TextColor.color(0x5a83f3))
            )

        return listOf(waterText, waterBar)
    }

    fun addRecipe() {
        val recipe = ShapedRecipe(ITEM_KEY, getItem())
        recipe.shape(" B ")
        recipe.setIngredient('B', Material.BUCKET)
        Bukkit.addRecipe(recipe)
    }

    fun removeRecipe() {
        Bukkit.removeRecipe(ITEM_KEY)
    }
}
