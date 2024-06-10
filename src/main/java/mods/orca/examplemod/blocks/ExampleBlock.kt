package mods.orca.examplemod.blocks

import mods.orca.examplemod.ExampleMod
import mods.orca.examplemod.gui.tabs.ExampleCreativeTab
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.item.ItemBlock

object ExampleBlock : Block(Material.ICE), IHasItemBlock {

    private const val NAME = "example_block"
    override val itemBlock = ItemBlock(this).apply { setRegistryName(NAME) }

    init {
        setRegistryName(NAME)
        setTranslationKey(ExampleMod.translationKey(NAME))
        setCreativeTab(ExampleCreativeTab)
    }

}
