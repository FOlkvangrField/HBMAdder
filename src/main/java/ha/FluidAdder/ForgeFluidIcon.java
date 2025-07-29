package ha.FluidAdder;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FluidTraitSimple;
import com.hbm.render.icon.RGBMutatorInterpolatedComponentRemap;
import com.hbm.render.icon.TextureAtlasSpriteMutatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.*;

import static ha.HBMAddon.MODID;

public class ForgeFluidIcon {
    public static HashMap<String, IIcon> stillIconMap = new HashMap();
    public static HashMap<String, IIcon> flowIconMap = new HashMap();
    public static int ColorEdit(int defColor,double edit){
        // 提取RGB值
        int red = (defColor >> 16) & 0xFF; // 获取红色分量
        int green = (defColor >> 8) & 0xFF; // 获取绿色分量
        int blue = defColor & 0xFF; // 获取蓝色分量
        red *= edit;green *= edit;blue *= edit;
        return (int)(Math.min(red,255) << 16)|(int)(Math.min(green,255) << 8)|(int)(Math.min(blue,255));
    }
    public static FluidIcon getFluidIcon(TextureMap map, String fluidName){
        //boolean iiconLocation = doesTextureExist("/assets/ha/textures/blocks/" + fluidName + "_still.png");
        IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
        ResourceLocation stillIconLocation = new ResourceLocation(MODID,"textures/blocks/" + fluidName + "_still.png");
        ResourceLocation flowIconLocation = new ResourceLocation(MODID,"textures/blocks/" + fluidName + "_flowing.png");
        boolean hasFlowIconLocation = false;

        try {
            rm.getResource(flowIconLocation);
            hasFlowIconLocation = true;
        } catch (IOException ignored) {}
        try {
            rm.getResource(stillIconLocation);
            IIcon still = map.registerIcon("ha:" + fluidName + "_still");
            IIcon flowing = still;
            if(hasFlowIconLocation){
                flowing = map.registerIcon("ha:" + fluidName + "_flowing");
            }
            return new FluidIcon(still,flowing);
        } catch (IOException e) {
            String name;
            FluidType hbmFluid = Fluids.fromName(fluidName.toUpperCase());
            RGBMutatorInterpolatedComponentRemap iconColor = new RGBMutatorInterpolatedComponentRemap(0xFFFFFF, 0x505050, ColorEdit(hbmFluid.getColor(), 1.5), ColorEdit(hbmFluid.getColor(), 0.5));
            name = "ha:custom_water-";
            if (hbmFluid.hasTrait(FluidTraitSimple.FT_Viscous.class)) {
                name = "ha:custom_oil-";
            }
            if (hbmFluid.temperature > 1000 || hbmFluid.hasTrait(FluidTraitSimple.FT_Amat.class) || hbmFluid.hasTrait(FluidTraitSimple.FT_Plasma.class)) {
                name = "ha:custom_lava-";
            }
            TextureAtlasSpriteMutatable mutatableIcon = new TextureAtlasSpriteMutatable(name + fluidName, iconColor).setBlockAtlas();
            map.setTextureEntry(name + fluidName, mutatableIcon);
            return new FluidIcon(mutatableIcon,mutatableIcon);
        }
    }
    public static class FluidIcon {
        public IIcon stillIcon;
        public IIcon flowingIcon;

        public FluidIcon(IIcon stillIcon, IIcon flowingIcon) {
            this.stillIcon = stillIcon;
            this.flowingIcon = flowingIcon;
        }
    }
}
