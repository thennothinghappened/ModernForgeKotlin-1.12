package mods.orca.examplemod.proxy

import net.minecraft.item.Item

/**
 * Proxy for exposing a common set of behaviours with side-specific implementations.
 */
abstract class Proxy {

    /**
     * Run on the mod's PreInit event.
     */
    open fun preInit() {

    }

    /**
     * Run on the mod's Init event.
     */
    open fun init() {

    }

    /**
     * Run after mod initialization.
     */
    open fun postInit() {

    }

    /**
     * Register a renderer for the given [Item].
     */
    open fun registerItemRenderer(item: Item, metadata: Int, id: String) {}

}
