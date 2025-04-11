package HA.Fluiddder;

import HA.Blocks.ForgeFluid;
import HA.HBMAddon;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
import com.hbm.render.util.EnumSymbol;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ForkJoinPool;


public class ForgeFluidAdder extends Fluids {
    static HashMap<String, String> en_USnames = new HashMap<>(), zh_CNnames = new HashMap<>();
    public static List<Fluid> forgeFluids = new ArrayList();
    static List<String> namespace=new ArrayList<>();
    private IIcon icon;
    public static Map<String, Fluid> forgecache = FluidRegistry.getRegisteredFluids();
    public static List<FluidType> hbmFluidsType = metaOrder;
    public static void construct(){
        for(int a=0;a<Storage.hbmStorage.size();a++){
            Storage.hbmModel model=Storage.hbmStorage.get(a);
            if(namespace.contains(model.name)) {
                System.out.println("Duplicate name: " + model.name);
                continue;
            }
            Fluid fluid = new NewForgeFluid(model.name).setTemperature(model.temperature).setUnlocalizedName(model.name);
            forgeFluids.add(fluid);
        }
        /*for(FluidType type : hbmFluidsType){
            if(forgecache.get(type.getName().toLowerCase())==null&&type!=Fluids.NONE){
                Fluid fluid = new NewForgeFluid(type.getName().toLowerCase()).setTemperature(type.temperature).setUnlocalizedName(type.getName().toLowerCase());
                forgeFluids.add(fluid);
            }
        }*/
        for(Fluid forgeFluid : forgeFluids){
            FluidRegistry.registerFluid(forgeFluid);
            //Block block = new ForgeFluid(forgeFluid,new MaterialLiquid(MapColor.airColor),forgeFluid.getName()).setBlockName(forgeFluid.getName() + "_block");
            //fluidBlocks.add(block);
        }
    }
    public static void makeLocalized() {
        for (Fluid addedForgeFluid : forgeFluids) {
            //BufferedImage
            String forgeUnlocal = addedForgeFluid.getUnlocalizedName();
            FluidType hbmFluid = Fluids.fromName(addedForgeFluid.getName().toUpperCase());
            String hbmUnlocal = hbmFluid.getUnlocalizedName();

            //if (fluid == null|| en_USnames.containsKey(unlocal)) continue;
            //String temp = fluid.getUnlocalizedName();
            if (hbmFluid.customFluid) {
                en_USnames.put(forgeUnlocal, hbmFluid.getLocalizedName());
                zh_CNnames.put(forgeUnlocal, hbmFluid.getLocalizedName());
            } else if (StatCollector.canTranslate(hbmUnlocal)) {
                en_USnames.put(forgeUnlocal, LanguageRegistry.instance().getStringLocalization(hbmUnlocal, "en_US"));
                zh_CNnames.put(forgeUnlocal, LanguageRegistry.instance().getStringLocalization(hbmUnlocal, "zh_CN"));
            } else {
                en_USnames.put(forgeUnlocal, addedForgeFluid.getName());
                zh_CNnames.put(forgeUnlocal, addedForgeFluid.getName());
            }
        }

        LanguageRegistry.instance().injectLanguage("en_US", en_USnames);
        LanguageRegistry.instance().injectLanguage("zh_CN", zh_CNnames);
    }
}
