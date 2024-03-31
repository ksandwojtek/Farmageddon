package ksndq.farmageddon.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import ksndq.farmageddon.Farmageddon.Companion.gogglesStandKey
import ksndq.farmageddon.Farmageddon.Companion.itemIDKey
import ksndq.farmageddon.utils.UUIDDataType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.math.roundToInt

class GogglesListener(private val plugin: JavaPlugin) : Listener {

    private val runnableMap = HashMap<UUID, BukkitRunnable>()

    @EventHandler
    fun onArmorChange(event: PlayerArmorChangeEvent) {
        val player = event.player
        val newItem = event.newItem
        val oldItem = event.oldItem

        if (isGogglesItem(oldItem)) {
            stopGogglesRunnable(player)
        } else if (isGogglesItem(newItem)) {
            startGogglesRunnable(player)
        }
    }

    private fun isGogglesItem(item: ItemStack?): Boolean {
        return item?.itemMeta?.persistentDataContainer?.get(itemIDKey!!, PersistentDataType.STRING) == "FARMERS_GOGGLES"
    }

    private fun stopGogglesRunnable(player: Player) {
        runnableMap[player.uniqueId]?.cancel()
        runnableMap.remove(player.uniqueId)
        player.persistentDataContainer[gogglesStandKey!!, UUIDDataType()]?.let { Bukkit.getEntity(it) }?.remove()
    }

    private fun startGogglesRunnable(player: Player) {
        val gogglesRunnable = object : BukkitRunnable() {
            private var textStand: ArmorStand? = null
            private var currentCrop: Block? = null

            override fun run() {
                val targetBlock = player.getTargetBlockExact(5) ?: return

                if (targetBlock != currentCrop) {
                    textStand?.remove()
                    currentCrop = targetBlock
                    if (targetBlock.blockData is Ageable) {
                        val cropData = targetBlock.blockData as Ageable
                        val progress = ((cropData.age.toDouble() / cropData.maximumAge) * 100).roundToInt()
                        val progressText = if (progress == 100) "§aMature" else "§r$progress%"
                        val finalLocation = targetBlock.location.add(0.5, 0.75, 0.5)
                        textStand = createArmorStand(finalLocation, "§7Growth: $progressText")
                        player.showEntity(plugin, textStand as Entity)
                        player.persistentDataContainer[gogglesStandKey!!, UUIDDataType()] = textStand!!.uniqueId
                    }
                }
            }
        }

        gogglesRunnable.runTaskTimer(plugin, 0, 2)
        runnableMap[player.uniqueId] = gogglesRunnable
    }

    private fun createArmorStand(location: Location, text: String): ArmorStand {
        val armorStand = location.world.spawn(location, ArmorStand::class.java) { stand ->
            stand.isVisible = false
            stand.isMarker = true
            stand.customName = text
            stand.setGravity(false)
            stand.isCustomNameVisible = true
            stand.isVisibleByDefault = false
        }
        return armorStand
    }
}
