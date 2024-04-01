package ksndq.farmageddon.blocks

import ksndq.farmageddon.Farmageddon
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

object Sprinkler {
    private const val ITEM_ID = "SPRINKLER"
    private val NAMESPACEDKEY = NamespacedKey(Farmageddon.plugin, "sprinkler")
    private val WATER_LEVEL = NamespacedKey(Farmageddon.plugin, "water-level")
    fun getItem(): ItemStack {
        val item = ItemStack(Material.DISPENSER)
        val itemMeta = item.itemMeta
        itemMeta.addItemFlags(
            ItemFlag.HIDE_ENCHANTS,
        )
        itemMeta.displayName(
            Component.text().content("Sprinkler").color(
                TextColor.color(0x5a83f3)
            ).build()
        )

        val waterTextLore = Component.text().content("Water Level:").color(TextColor.color(0xAAAAAA)).build()
        val waterBarLore = Component.text().content("■■■■■■■■■■ 0%").color(TextColor.color(0x555555)).build()

        itemMeta.lore(listOf(waterTextLore, waterBarLore))

        itemMeta.persistentDataContainer[Farmageddon.itemIDKey!!, PersistentDataType.STRING] = ITEM_ID
        itemMeta.persistentDataContainer[WATER_LEVEL, PersistentDataType.INTEGER] = 0

        itemMeta.addEnchant(Enchantment.DURABILITY, 1, false)

        item.itemMeta = itemMeta
        return item
    }

    fun addRecipe() {
        val recipe = ShapedRecipe(NAMESPACEDKEY, getItem())
//        recipe.shape("CCC","CRC", "IBI")
        recipe.shape(" B ")
//        recipe.setIngredient('I', Material.IRON_BLOCK)
        recipe.setIngredient('B', Material.BUCKET)
//        recipe.setIngredient('C', Material.COBBLESTONE)
//        recipe.setIngredient('R', Material.REDSTONE_BLOCK)
        Bukkit.addRecipe(recipe)
    }

    fun removeRecipe() {
        Bukkit.removeRecipe(NAMESPACEDKEY)
    }
}