package ksndq.farmageddon.listeners.items

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import ksndq.farmageddon.Farmageddon.Companion.gogglesStandKey
import ksndq.farmageddon.Farmageddon.Companion.itemIDKey
import ksndq.farmageddon.utils.ItemUtils
import ksndq.farmageddon.utils.UUIDDataType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.BlockData
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.math.roundToInt

class GogglesListener(private val plugin: JavaPlugin) : Listener {

    private val runnableMap = HashMap<UUID, BukkitRunnable>()
    private val TASK_PERIOD = 2L
    private val utils: ItemUtils = ItemUtils()

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

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if(isGogglesItem(player.inventory.helmet)) startGogglesRunnable(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        stopGogglesRunnable(event.player)
    }

    private fun isGogglesItem(item: ItemStack?): Boolean {
        return utils.hasID(item, "FARMERS_GOGGLES")
    }

    private fun stopGogglesRunnable(player: Player) {
        runnableMap[player.uniqueId]?.cancel()
        runnableMap.remove(player.uniqueId)
        player.persistentDataContainer[gogglesStandKey!!, UUIDDataType()]?.let { uuid ->
            Bukkit.getEntity(uuid)?.remove()
        }
    }

    private fun startGogglesRunnable(player: Player) {
        val gogglesRunnable = object : BukkitRunnable() {
            private var textStand: ArmorStand? = null
            private var currentCrop: BlockData? = null
            private var currentLocation: Location? = null

            override fun run() {
                val targetBlock = player.getTargetBlockExact(5)
                if (targetBlock == null || targetBlock.type == Material.CACTUS || targetBlock.type == Material.SUGAR_CANE) {
                    textStand?.remove()
                    currentCrop = null
                    currentLocation = null
                    player.persistentDataContainer.remove(gogglesStandKey!!)
                    return
                } else if (targetBlock.blockData != currentCrop || targetBlock.location != currentLocation) {
                    textStand?.remove()
                    currentCrop = targetBlock.blockData
                    currentLocation = targetBlock.location
                    (targetBlock.blockData as? Ageable)?.let { cropData ->
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

        gogglesRunnable.runTaskTimer(plugin, 0, TASK_PERIOD)
        runnableMap[player.uniqueId] = gogglesRunnable
    }

    private fun createArmorStand(location: Location, text: String): ArmorStand {
        return location.world.spawn(location, ArmorStand::class.java) { stand ->
            stand.isVisible = false
            stand.isMarker = true
            stand.customName = text
            stand.setGravity(false)
            stand.isCustomNameVisible = true
            stand.isVisibleByDefault = false
        }
    }
}
