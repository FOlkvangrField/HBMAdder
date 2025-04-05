package HA.Fluiddder;

import HA.Blocks.ForgeFluid;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.lib.RefStrings;
import com.hbm.main.MainRegistry;
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
    //public static List<FluidType> hbmFluids = metaOrder;
    private IIcon icon;
    public static Map<String, Fluid> forgecache = FluidRegistry.getRegisteredFluids();
    public static void construct(){

        //Minecraft.getMinecraft().getTextureMapBlocks().registerIcon("HA:textures/blocks/custom_water.png");//.setTextureEntry("HA:textures/blocks/custom_water.png",new ForgeFluidIcon("HA:textures/blocks/custom_water.png"));
        //Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("HA:textures/blocks/custom_water.png"));
        //TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("HA:textures/blocks/custom_water.png");
        for(FluidType type : metaOrder){
            if(forgecache.get(type.getName().toLowerCase())==null&&type!=Fluids.NONE){
                //this.icon = map.registerIcon(this.type.getTexture().toString());
                Fluid fluid = new NewForgeFluid(type.getName().toLowerCase()).setTemperature(type.temperature).setUnlocalizedName(type.getName().toLowerCase());
                //IIcon stillIcon = ForgeFluidIcon.getIcon(type);//.createColoredIcon("HA:textures/blocks/custom_water.png", type.getColor()); // 灰度图 + 颜色
                //fluid.setIcons(stillIcon,stillIcon);

                forgeFluids.add(fluid);

            }
        }
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
            if (StatCollector.canTranslate(hbmUnlocal)) {
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
