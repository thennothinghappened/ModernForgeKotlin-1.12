package mods.orca.examplemod;

import mods.orca.examplemod.proxy.Proxy;
import mods.orca.examplemod.registry.RegistryHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
@Mod(
    modid = ExampleMod.modId,
    name = "Example Mod",
    version = "0.1.0"
)
public class ExampleMod {

    public static final String modId = "examplemod";
    public static Logger logger;

    @Mod.Instance(modId)
    public static ExampleMod INSTANCE;

    @SidedProxy(
        clientSide = "mods.orca.examplemod.proxy.ClientProxy",
        serverSide = "mods.orca.examplemod.proxy.DedicatedProxy"
    )
    public static Proxy proxy;

    public ExampleMod() {
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(RegistryHandler.class);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        logger = event.getModLog();
        logger.info("Mod {} is now loading!", modId);

        proxy.preInit();

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    /**
     * Get the translation key for the given path.
     *
     * @param path "Path" of the translation key elements.
     * @return Fully qualified translation key string.
     */
    public static String translationKey(@Nonnull final String... path) {
        return modId + "." + String.join(".", path);
    }

    /**
     * Get a resource location for this mod.
     *
     * @param path Path to the resource within this mod's assets.
     * @return ResourceLocation pointing to the given path.
     */
    public static ResourceLocation resource(@Nonnull final String path) {
        return new ResourceLocation(modId, path);
    }

}
