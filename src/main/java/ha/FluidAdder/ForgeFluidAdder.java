package ha.FluidAdder;

import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.trait.*;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.*;


public class ForgeFluidAdder extends Fluids {
    static HashMap<String, String> en_USnames = new HashMap<>(), zh_CNnames = new HashMap<>();
    public static List<Fluid> forgeFluids = new ArrayList();
    static List<String> namespace=new ArrayList<>();

    public static List<FluidType> hbmFluidsType = metaOrder;
    public static void construct(){
        for(int a=0;a<Storage.hbmStorage.size();a++){
            Storage.hbmModel model=Storage.hbmStorage.get(a);
            if(namespace.contains(model.name)) {
                System.out.println("Duplicate name: " + model.name);
                continue;
            }
            Fluid forgeFluid = new NewForgeFluid(model.name,model.hasBlock).setTemperature(model.temperature).setUnlocalizedName(model.name);

            forgeFluids.add(forgeFluid);
        }
        for(Fluid forgeFluid : forgeFluids){
            FluidType hbmFluid = Fluids.fromName(forgeFluid.getName().toUpperCase());
            Material material = new MaterialLiquid(MapColor.brownColor);
            if (hbmFluid.temperature > 1000 || hbmFluid.hasTrait(FluidTraitSimple.FT_Amat.class) || hbmFluid.hasTrait(FluidTraitSimple.FT_Plasma.class)) {
                forgeFluid.setLuminosity(15);
                material = Material.lava;
            }
            if (hbmFluid.hasTrait(FluidTraitSimple.FT_Viscous.class)) {
                forgeFluid.setViscosity(3000);
            }
            if (hbmFluid.hasTrait(FluidTraitSimple.FT_Gaseous_ART.class)) {
                forgeFluid.setGaseous(true);
            }
            FluidRegistry.registerFluid(forgeFluid);
            if(((NewForgeFluid)forgeFluid).hasBlock){
                Block fluidBlock = new ForgeFluidBlocks(forgeFluid, material, forgeFluid.getName(),MapColor.airColor).setBlockName(forgeFluid.getName() + "_block");
                if(forgeFluid.getViscosity()==3000){
                    ((ForgeFluidBlocks)fluidBlock).setViscosity(true);
                }
                if(hbmFluid.hasTrait(FT_VentRadiation.class)){
                    ((ForgeFluidBlocks)fluidBlock).setRadiation(hbmFluid.getTrait(FT_VentRadiation.class).getRadPerMB());
                }
                if(hbmFluid.hasTrait(FT_Corrosive.class)){
                    ((ForgeFluidBlocks)fluidBlock).setCorrosive(hbmFluid.getTrait(FT_Corrosive.class).getRating());
                }
                GameRegistry.registerBlock(fluidBlock, fluidBlock.getUnlocalizedName());
            }
        }
        makeLocalized(forgeFluids);
    }
    public static void makeLocalized(List<Fluid> forgeFluids) {
        for (Fluid addedForgeFluid : forgeFluids) {
            //BufferedImage
            String forgeUnlocal = addedForgeFluid.getUnlocalizedName();
            FluidType hbmFluid = Fluids.fromName(addedForgeFluid.getName().toUpperCase());
            String hbmUnlocal = hbmFluid.getUnlocalizedName();
            String blockUnlocal = null;
            if (((NewForgeFluid) addedForgeFluid).hasBlock) {
                blockUnlocal = addedForgeFluid.getBlock().getUnlocalizedName();
            }
            if (hbmFluid.renderWithTint) {
                en_USnames.put(forgeUnlocal, hbmFluid.getLocalizedName());
                zh_CNnames.put(forgeUnlocal, hbmFluid.getLocalizedName());
                if (blockUnlocal != null) {
                    en_USnames.put(blockUnlocal + ".name", hbmFluid.getLocalizedName());
                    zh_CNnames.put(blockUnlocal + ".name", hbmFluid.getLocalizedName());
                }
            } else if (StatCollector.canTranslate(hbmUnlocal)) {
                en_USnames.put(forgeUnlocal, LanguageRegistry.instance().getStringLocalization(hbmUnlocal, "en_US"));
                zh_CNnames.put(forgeUnlocal, LanguageRegistry.instance().getStringLocalization(hbmUnlocal, "zh_CN"));
                if (blockUnlocal != null) {
                    en_USnames.put(blockUnlocal + ".name", LanguageRegistry.instance().getStringLocalization(hbmUnlocal, "en_US"));
                    zh_CNnames.put(blockUnlocal + ".name", LanguageRegistry.instance().getStringLocalization(hbmUnlocal, "zh_CN"));
                }
            } else {
                en_USnames.put(forgeUnlocal, addedForgeFluid.getName());
                zh_CNnames.put(forgeUnlocal, addedForgeFluid.getName());
            }
        }

        LanguageRegistry.instance().injectLanguage("en_US", en_USnames);
        LanguageRegistry.instance().injectLanguage("zh_CN", zh_CNnames);
    }
}
