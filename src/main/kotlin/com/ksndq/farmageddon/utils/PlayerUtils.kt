package com.ksndq.farmageddon.utils

import org.bukkit.entity.Player

object PlayerUtils {
    fun getPlayerNormalizedRotation(player: Player): String {
        val yaw: Float = (player.location.yaw + 360f) % 360f

        return when {
            yaw < 45 || yaw >= 315 -> "SOUTH"
            yaw < 135 -> "WEST"
            yaw < 225 -> "NORTH"
            yaw < 315 -> "EAST"
            else -> "NORTH"
        }
    }
}