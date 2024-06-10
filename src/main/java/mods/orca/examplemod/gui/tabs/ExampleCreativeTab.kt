package mods.orca.examplemod.gui.tabs

import mods.orca.examplemod.ExampleMod
import mods.orca.examplemod.blocks.ExampleBlock
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object ExampleCreativeTab : CreativeTabs(ExampleMod.modId) {

    override fun createIcon() = ItemStack(Item.getItemFromBlock(ExampleBlock))

}
