package mods.orca.examplemod.registry

import mods.orca.examplemod.ExampleMod
import mods.orca.examplemod.blocks.ExampleBlock
import mods.orca.examplemod.blocks.IHasItemBlock
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Handler to register our set of blocks and items and so forth with Forge.
 */
@Suppress("unused")
object RegistryHandler {

    /**
     * Set of blocks we know of to register.
     */
    private val blocks: Set<Block> = setOf(
        ExampleBlock
    )

    /**
     * Set of items we know of to register.
     */
    private val items: Set<Item> = emptySet(

    )

    /**
     * Set of ItemBlocks to register.
     */
    private val itemBlocks: Set<ItemBlock> = blocks
        .mapNotNull { it as? IHasItemBlock }
        .map { it.itemBlock }
        .toSet()
        .onEach { println(it) }

    /**
     * Register our list of items.
     */
    @JvmStatic
    @SubscribeEvent
    fun onItemRegister(event: RegistryEvent.Register<Item>) {
        items.forEach(event.registry::register)
        itemBlocks.forEach(event.registry::register)
    }

    /**
     * Register our list of blocks.
     */
    @JvmStatic
    @SubscribeEvent
    fun onBlockRegister(event: RegistryEvent.Register<Block>) {
        blocks.forEach(event.registry::register)
    }

    /**
     * Register model renderers for our items and blocks.
     */
    @JvmStatic
    @SubscribeEvent
    fun onModelRegister(event: ModelRegistryEvent) {

        items.forEach {
            ExampleMod.proxy.registerItemRenderer(it, 0, "inventory")
        }

        itemBlocks.forEach {
            ExampleMod.proxy.registerItemRenderer(it, 0, "inventory")
        }

    }

}
