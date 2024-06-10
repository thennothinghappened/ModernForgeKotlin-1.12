package mods.orca.examplemod.blocks

import net.minecraft.item.ItemBlock

/**
 * A block which has an ItemBlock for the inventory.
 */
interface IHasItemBlock {
    val itemBlock: ItemBlock
}
