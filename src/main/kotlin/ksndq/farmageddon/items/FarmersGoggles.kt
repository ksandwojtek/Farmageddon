package ksndq.farmageddon.items

import ksndq.farmageddon.Farmageddon.Companion.farmersGogglesItemKey
import ksndq.farmageddon.Farmageddon.Companion.itemIDKey
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

object FarmersGoggles {
    private const val ITEM_ID = "FARMERS_GOGGLES"
    fun getItem(): ItemStack {
        val item = ItemStack(Material.LEATHER_HELMET)
        val itemMeta = item.itemMeta
        itemMeta.isUnbreakable = true
        itemMeta.addItemFlags(
            ItemFlag.HIDE_ENCHANTS,
            ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE,
            ItemFlag.HIDE_DYE
        )
        itemMeta.displayName(Component.text().content("Farmer's Goggles").color(TextColor.color(0xdcba64)).build())

        val modifier = AttributeModifier(
            UUID.randomUUID(),
            "Base Armor Reduction",
            -1.0,
            AttributeModifier.Operation.ADD_SCALAR,
            EquipmentSlot.HEAD
        )
        itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, modifier)

        val leatherMeta = itemMeta as LeatherArmorMeta
        leatherMeta.setColor(Color.fromRGB(220, 186, 100))

        itemMeta.persistentDataContainer[itemIDKey!!, PersistentDataType.STRING] = ITEM_ID

        item.itemMeta = leatherMeta
        return item
    }

    fun addRecipe() {
        val recipe = ShapedRecipe(farmersGogglesItemKey!!, getItem())
        recipe.shape(" S ", "GLG")
        recipe.setIngredient('S', Material.STRING)
        recipe.setIngredient('G', Material.GLASS)
        recipe.setIngredient('L', Material.LEATHER)
        Bukkit.addRecipe(recipe)
    }
}