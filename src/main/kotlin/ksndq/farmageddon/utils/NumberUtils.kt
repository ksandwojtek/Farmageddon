package ksndq.farmageddon.utils

import java.util.*
import kotlin.Comparator

object NumberUtils {
    private val map: TreeMap<Int, String> = TreeMap(Comparator.reverseOrder())

    init {
        map[1000] = "M"
        map[900] = "CM"
        map[500] = "D"
        map[400] = "CD"
        map[100] = "C"
        map[90] = "XC"
        map[50] = "L"
        map[40] = "XL"
        map[10] = "X"
        map[9] = "IX"
        map[5] = "V"
        map[4] = "IV"
        map[1] = "I"
    }

    fun toRoman(number: Int): String {
        val largest = map.floorKey(number) ?: return ""
        return if (number == largest) {
            map[largest]!!
        } else {
            map[largest]!! + toRoman(number - largest)
        }
    }
}