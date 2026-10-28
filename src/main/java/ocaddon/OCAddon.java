package ocaddon;

import li.cil.oc.api.Driver;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ocaddon.driver.DriverCooler;
import ocaddon.sim.ThermalHandler;

@Mod(modid = OCAddon.MODID, name = "OpenCooler", version = "1.0.0",
     dependencies = "required-after:opencomputers")
@EventBusSubscriber
public class OCAddon {
    public static final String MODID = "ocaddon";

    @EventHandler
    public void init(FMLInitializationEvent e) {
        Driver.add(new DriverCooler());
        MinecraftForge.EVENT_BUS.register(new ThermalHandler());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> e) {
        e.getRegistry().registerAll(ModItems.ALL);
    }

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> e) {
        e.getRegistry().registerAll(
            new ocaddon.item.RecipeCpuPaste(true).setRegistryName(MODID, "apply_paste"),
            new ocaddon.item.RecipeCpuPaste(false).setRegistryName(MODID, "remove_paste"));
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        for (Item i : ModItems.ALL)
            ModelLoader.setCustomModelResourceLocation(i, 0,
                new ModelResourceLocation(i.getRegistryName(), "inventory"));
    }
}
