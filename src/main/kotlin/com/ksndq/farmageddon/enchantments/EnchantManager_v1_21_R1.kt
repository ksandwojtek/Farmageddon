@file:Suppress("ClassName")

package com.ksndq.farmageddon.enchantments

import com.ksndq.farmageddon.Farmageddon
import com.ksndq.farmageddon.utils.Reflex.getFieldValue
import com.ksndq.farmageddon.utils.Reflex.setFieldValue
import net.minecraft.core.Holder
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.tags.EnchantmentTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.item.Item
import net.minecraft.world.item.enchantment.Enchantment
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.craftbukkit.v1_21_R1.CraftEquipmentSlot
import org.bukkit.craftbukkit.v1_21_R1.CraftServer
import org.bukkit.craftbukkit.v1_21_R1.util.CraftNamespacedKey
import org.bukkit.inventory.EquipmentSlot
import java.io.File
import java.util.*


object EnchantManager_v1_21_R1 {

    private const val HOLDER_SET_NAMED_CONTENTS_FIELD = "c"
    private const val HOLDER_REFERENCE_TAGS_FIELD = "b"
    private const val HOLDER_SET_DIRECT_CONTENTS_FIELD = "b"

    private val HOE_MATERIALS = setOf(
        Material.WOODEN_HOE,
        Material.STONE_HOE,
        Material.GOLDEN_HOE,
        Material.IRON_HOE,
        Material.DIAMOND_HOE,
        Material.NETHERITE_HOE
    )

    private val AXE_MATERIALS = setOf(
        Material.WOODEN_AXE,
        Material.STONE_AXE,
        Material.GOLDEN_AXE,
        Material.IRON_AXE,
        Material.DIAMOND_AXE,
        Material.NETHERITE_AXE
    )

    fun registerEnchantments() {
        setFieldValue(enchantmentRegistry, "l", false)
        setFieldValue(enchantmentRegistry, "m", IdentityHashMap<Any, Any>())

        val file = File(Farmageddon.getInstance().dataFolder, "enchantments.yml")
        val enchantsConfig = YamlConfiguration.loadConfiguration(file)

        val replenishConfig = enchantsConfig["replenish"] as MemorySection
        if(replenishConfig["enabled"] as Boolean) {
            registerEnchant(
                enchantId = "replenish",
                name = "Replenish",
                weight = replenishConfig["weight"] as Int,
                maxLevel = 1,
                minCost = 1,
                maxCost = 12,
                anvilCost = 8,
                slots = arrayOf(EquipmentSlot.HAND),
                applicableItemSet = (HOE_MATERIALS union AXE_MATERIALS).toMutableSet(),
                conflicts = replenishConfig["conflicts"] as ArrayList<String>,
                isCurse = replenishConfig["isCurse"] as? Boolean ?: false,
                isTreasure = replenishConfig["isTreasure"] as? Boolean ?: false,
                isTradeable = replenishConfig["isTradeable"] as? Boolean ?: false,
                isDiscoverable = replenishConfig["isFromEnchanting"] as? Boolean ?: true,
            )
        }

        val delicateConfig = enchantsConfig["delicate"] as MemorySection
        if(delicateConfig["enabled"] as Boolean) {
            registerEnchant(
                enchantId = "delicate",
                name = "Delicate",
                weight = delicateConfig["weight"] as Int,
                maxLevel = 1,
                minCost = 1,
                maxCost = 12,
                anvilCost = 8,
                slots = arrayOf(EquipmentSlot.HAND),
                applicableItemSet = (HOE_MATERIALS union AXE_MATERIALS).toMutableSet(),
                conflicts = delicateConfig["conflicts"] as ArrayList<String>,
                isCurse = delicateConfig["isCurse"] as? Boolean ?: false,
                isTreasure = delicateConfig["isTreasure"] as? Boolean ?: false,
                isTradeable = delicateConfig["isTradeable"] as? Boolean ?: false,
                isDiscoverable = delicateConfig["isFromEnchanting"] as? Boolean ?: true,
            )
        }

        enchantsConfig.save(file)

        enchantmentRegistry.freeze()
    }

    private val server: MinecraftServer = (Bukkit.getServer() as CraftServer).server
    private val enchantmentRegistry: Registry<Enchantment> =
        server.registryAccess().registry(Registries.ENCHANTMENT).orElse(null)

    private fun registerEnchant(
        enchantId: String,
        name: String,
        weight: Int,
        maxLevel: Int,
        minCost: Int,
        maxCost: Int,
        anvilCost: Int,
        slots: Array<EquipmentSlot>,
        applicableItemSet: MutableSet<Material>,
        conflicts: ArrayList<String>,
        isCurse: Boolean,
        isTreasure: Boolean,
        isTradeable: Boolean,
        isDiscoverable: Boolean
    ) {
        val key = key(enchantId)
        val component = Component.literal(name)
        val exclusiveSet: HolderSet<Enchantment> = HolderSet.direct()
        val effects = DataComponentMap.builder().build()

        val supportedItems = createItemSet(
            "enchant_supported",
            enchantId,
            applicableItemSet
        )
        val primaryItems = createItemSet(
            "enchant_primary",
            enchantId,
            applicableItemSet
        )
        val items: Registry<Item> = server.registryAccess().registry(Registries.ITEM).orElseThrow()

//        val supportedItems = items.getTag(ItemTags.DURABILITY_ENCHANTABLE).orElse(null)
//        val primaryItems = items.getTag(ItemTags.DURABILITY_ENCHANTABLE).orElse(null)

        val minCostCost = Enchantment.Cost(minCost, 11)
        val maxCostCost = Enchantment.Cost(maxCost, 11)

        val nmsSlots = nmsSlots(slots)

        val definition: Enchantment.EnchantmentDefinition = Enchantment.definition(
            supportedItems,
            primaryItems,
            weight,
            maxLevel,
            minCostCost,
            maxCostCost,
            anvilCost,
            nmsSlots[0]
        )

        val enchantment = Enchantment(component, definition, exclusiveSet, effects)

        val reference: Holder.Reference<Enchantment> = enchantmentRegistry.createIntrusiveHolder(enchantment)
        Registry.register(enchantmentRegistry, enchantId, enchantment)

        if (isCurse) {
            addInTag(EnchantmentTags.CURSE, reference)
        } else {
            if (isTreasure) {
                Farmageddon.getInstance().logger.warning("$enchantId: isTreasure is true.")
                addInTag(EnchantmentTags.TREASURE, reference)
            } else {
                addInTag(EnchantmentTags.NON_TREASURE, reference)
            }

            if (isTradeable) {
                Farmageddon.getInstance().logger.warning("$enchantId: isTreadable is true.")
                addInTag(EnchantmentTags.TRADEABLE, reference)
            } else {
                removeFromTag(EnchantmentTags.TRADEABLE, reference)
            }

            if (isDiscoverable) {
                Farmageddon.getInstance().logger.warning("$enchantId: isDiscoverable is true.")
                addInTag(EnchantmentTags.IN_ENCHANTING_TABLE, reference)
            } else {
                removeFromTag(EnchantmentTags.IN_ENCHANTING_TABLE, reference)
            }
        }

        addExclusives(enchantId, conflicts)
    }

    private fun nmsSlots(slots: Array<EquipmentSlot>): Array<EquipmentSlotGroup> {
        return Array(slots.size) { index ->
            val bukkitSlot = slots[index]
            CraftEquipmentSlot.getNMSGroup(bukkitSlot.group)
        }
    }

    private fun key(name: String): ResourceKey<Enchantment>? {
        return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace(name))
    }

    private fun addInTag(tagKey: TagKey<Enchantment>, reference: Holder.Reference<Enchantment>) {
//        println("Adding $reference to tag $tagKey")
        modifyTag(tagKey, reference) { list, ref ->
            list.add(ref)
//            println("Added $ref to list")
        }
    }

    private fun removeFromTag(tagKey: TagKey<Enchantment>, reference: Holder.Reference<Enchantment>) {
//        println("Removing $reference from tag $tagKey")
        modifyTag(tagKey, reference) { list, ref ->
            list.remove(ref)
//            println("Removed $ref from list")
        }
    }

    private fun modifyTag(
        tagKey: TagKey<Enchantment>,
        reference: Holder.Reference<Enchantment>,
        consumer: (MutableList<Holder<Enchantment>>, Holder.Reference<Enchantment>) -> Unit
    ) {
        val holders = enchantmentRegistry.getTag(tagKey).orElse(null)
//        println("Modifying tag $tagKey with reference $reference")
        if (holders == null) {
            Farmageddon.getInstance().logger.warning("$tagKey: Could not modify HolderSet. HolderSet is NULL.")
            return
        }

        val contentsBefore = ArrayList(getFieldValue(holders, HOLDER_SET_NAMED_CONTENTS_FIELD) as List<Holder<Enchantment>>)
//        println("Contents before modification: $contentsBefore")

        modifyHolderSetContents(holders, reference, consumer)

        val contentsAfter = ArrayList(getFieldValue(holders, HOLDER_SET_NAMED_CONTENTS_FIELD) as List<Holder<Enchantment>>)
//        println("Contents after modification: $contentsAfter")
    }


    @Suppress("UNCHECKED_CAST")
    private fun <T> modifyHolderSetContents(
        holders: HolderSet.Named<T>,
        reference: Holder.Reference<T>,
        consumer: (MutableList<Holder<T>>, Holder.Reference<T>) -> Unit
    ) {
        val contents = ArrayList(
            getFieldValue(holders, HOLDER_SET_NAMED_CONTENTS_FIELD) as List<Holder<T>>
        )
        consumer(contents, reference)
        setFieldValue(holders, HOLDER_SET_NAMED_CONTENTS_FIELD, contents)
    }


    private fun addExclusives(enchantId: String, exclusives: ArrayList<String>) {
        val enchantment = enchantmentRegistry[key(enchantId)]
        if (enchantment == null) {
            Farmageddon.getInstance().logger.warning("$enchantId: Could not set exclusive item list. Enchantment is not registered.")
            return
        }

        val exclusiveSet = enchantment.exclusiveSet()
        val contents = mutableListOf<Holder<Enchantment>>()

        exclusives.forEach { exclusiveEnchantId ->
            val key = key(exclusiveEnchantId)
            val reference = enchantmentRegistry.getHolder(key).orElse(null)
            if (reference != null) {
                contents.add(reference)
            }
        }

        setFieldValue(exclusiveSet, HOLDER_SET_DIRECT_CONTENTS_FIELD, contents)
    }

    @Suppress("UNCHECKED_CAST")
    private fun createItemSet(prefix: String, enchantId: String, materials: Set<Material>): HolderSet.Named<Item> {
        val items: Registry<Item> = server.registryAccess().registry(Registries.ITEM).orElseThrow()
        val customKey = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("$prefix/$enchantId"))
        val customItems = items.getOrCreateTag(customKey)
        val holders = mutableListOf<Holder<Item>>()

        materials.forEach { material ->
            val location = CraftNamespacedKey.toMinecraft(material.key)
            val holder = items.getHolder(location).orElse(null) ?: return@forEach

            val holderTags = getFieldValue(holder, HOLDER_REFERENCE_TAGS_FIELD) as MutableSet<TagKey<Item>>?
            val mutableHolderTags = holderTags?.toMutableSet() ?: mutableSetOf()
            mutableHolderTags.add(customKey)
            setFieldValue(holder, HOLDER_REFERENCE_TAGS_FIELD, mutableHolderTags)

            holders.add(holder)
        }

        setFieldValue(customItems, HOLDER_SET_NAMED_CONTENTS_FIELD, holders)

        return customItems
    }
}
