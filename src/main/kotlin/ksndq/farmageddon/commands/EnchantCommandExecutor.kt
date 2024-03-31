package ksndq.farmageddon.commands

import ksndq.farmageddon.Farmageddon
import ksndq.farmageddon.Farmageddon.Companion.maxLevels
import ksndq.farmageddon.enchantments.EnchantList
import ksndq.farmageddon.utils.NumberUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object EnchantCommandExecutor : CommandExecutor {

    private val axes = hashSetOf(
        Material.WOODEN_AXE,
        Material.STONE_AXE,
        Material.GOLDEN_AXE,
        Material.IRON_AXE,
        Material.DIAMOND_AXE,
        Material.NETHERITE_AXE
    )

    private val swords = hashSetOf(
        Material.WOODEN_SWORD,
        Material.STONE_SWORD,
        Material.GOLDEN_SWORD,
        Material.IRON_SWORD,
        Material.DIAMOND_SWORD,
        Material.NETHERITE_SWORD
    )

    private val boots = hashSetOf(
        Material.LEATHER_BOOTS,
        Material.CHAINMAIL_BOOTS,
        Material.GOLDEN_BOOTS,
        Material.IRON_BOOTS,
        Material.DIAMOND_BOOTS,
        Material.NETHERITE_BOOTS
    )

    private val leggings = hashSetOf(
        Material.LEATHER_LEGGINGS,
        Material.CHAINMAIL_LEGGINGS,
        Material.GOLDEN_LEGGINGS,
        Material.IRON_LEGGINGS,
        Material.DIAMOND_LEGGINGS,
        Material.NETHERITE_LEGGINGS
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true

        if (args.size != 2) {
            sender.sendMessage("Usage: /cenchant <enchant> <level>")
            return true
        }

        val enchantString = args[0]
        val enchantEnum = try {
            EnchantList.valueOf(enchantString.uppercase())
        } catch (e: IllegalArgumentException) {
            return true
        }

        val level = Integer.parseInt(args[1])

        if (level > maxLevels[enchantEnum]!!) {
            sender.sendMessage("Inputted level exceeds max level")
            return true
        }

        val currentHeldItem = sender.inventory.itemInMainHand
        if (!enchantCanBeAppliedToItem(sender, enchantEnum as? Comparable<Any>, currentHeldItem.type)) {
            return true
        }

        val replacementItem = applyEnchant(currentHeldItem, enchantString, level)
        sender.inventory.setItemInMainHand(replacementItem)

        return true
    }

    private fun applyEnchant(item: ItemStack, enchantmentName: String, level: Int): ItemStack {
        val meta = item.itemMeta ?: return item
        val key =
            NamespacedKey(JavaPlugin.getPlugin(Farmageddon::class.java), enchantmentName.lowercase().replace(" ", ""))
        meta.persistentDataContainer[key, PersistentDataType.INTEGER] = level

        val lore = meta.lore?.toMutableList() ?: mutableListOf()
        lore.removeAll { it.contains(enchantmentName) }
        lore.add(ChatColor.GRAY.toString() + normalizeName(enchantmentName) + " " + NumberUtils.toRoman(level))

        meta.lore = lore
        item.itemMeta = meta
        return item
    }

    fun normalizeName(str: String): String {
        if (str.equals("lightsteps", ignoreCase = true)) {
            return "Light Steps"
        }
        return str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1).lowercase(Locale.getDefault())
    }

    fun enchantCanBeAppliedToItem(player: Player, enchant: Comparable<Any>?, currentItem: Material): Boolean {
        if (enchant == null) return false
        return when (enchant) {
            EnchantList.LIGHTSTEPS -> {
                if (!boots.contains(currentItem)) {
                    player.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD.toString() + "Can only be applied to boots")
                    false
                } else {
                    true
                }
            }
            else -> {
                true
            }
        }
    }

}