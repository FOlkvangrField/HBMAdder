package HA;

import HA.Config.Config;
import HA.Converter.ConverterBlock;
import HA.Converter.TransferRecipe;
import HA.Fluiddder.FluidAdder;
import HA.Fluiddder.ForgeFluidAdder;
import com.hbm.inventory.fluid.Fluids;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;


public class CommonProxy {
    Block transfer;
    //Block counter;

    public void preInit(FMLPreInitializationEvent event) {
        new Config(event.getSuggestedConfigurationFile());
        new Event();
        Loader.setFolder(event.getModConfigurationDirectory());
        transfer = new ConverterBlock();

        /*for(Block block : fluidBlocks){
            GameRegistry.registerBlock(block, block.getUnlocalizedName());
        }*/
        //counter = new BlockCounter();
    }

    public void init(FMLInitializationEvent event) {
        //Fluids.init();
        Loader.loadFluidFromJson(true);
        //FluidAdder.construct();
        ForgeFluidAdder.construct();
        ForgeFluidAdder.makeLocalized();
        Loader.loadRecipeFromJson(true);
        TransferRecipe.Construct();
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    public void gameExit(FMLServerStoppingEvent event) {
        if (Config.first.getBoolean()) {
            Config.set(Config.first, false);
            Config.set(Config.custom, false);
        }
        if (!Config.alwaysRefreshFluid) Config.set(Config.nFluid, false);
        if(!Config.alwaysRefreshRecipe)Config.set(Config.nRecipe, false);
    }
}
