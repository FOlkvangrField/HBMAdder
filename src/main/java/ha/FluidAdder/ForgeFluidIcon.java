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
                //mutatableIcon = new TextureAtlasSpriteMutatable(name + fluidName, iconColor).setBlockAtlas();
            }
            if (hbmFluid.temperature > 1000 || hbmFluid.hasTrait(FluidTraitSimple.FT_Amat.class) || hbmFluid.hasTrait(FluidTraitSimple.FT_Plasma.class)) {
                name = "ha:custom_lava-";
                //mutatableIcon = new TextureAtlasSpriteMutatable(name + fluidName, iconColor).setBlockAtlas();
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

    /*public ForgeFluidIcon(String iconName, RGBMutator mutator) {
        super(iconName);
        this.mutator = mutator;
    }*/

    /*public ForgeFluidIcon setBlockAtlas() {
        this.mipmap = Minecraft.getMinecraft().gameSettings.mipmapLevels;
        this.anisotropic = Minecraft.getMinecraft().gameSettings.anisotropicFiltering;
        this.basePath = "textures/blocks";
        return this;
    }*/
    /*@Override
    public void loadSprite(BufferedImage[] frames, AnimationMetadataSection animMeta, boolean anisotropicFiltering) {

        if(mutator != null) {
            for(int i = 0; i < frames.length; i++) {
                BufferedImage frame = frames[i];

                if(frame != null) mutator.mutate(frame, i, frames.length);
            }
        }

        super.loadSprite(frames, animMeta, anisotropicFiltering);
    }

    @Override
    public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
        return true; //YES!
    }

    @Override
    public boolean load(IResourceManager man, ResourceLocation resourcelocation) {

        String pathName = resourcelocation.getResourcePath();
        String tunkatedPath = pathName.substring(0, pathName.indexOf('-')); //fuck regex
        //so basically we remove the dash and everything trailing it (see ItemAutogen.java), this allows us to have unique icon names for what is actually the same icon file
        resourcelocation = new ResourceLocation(resourcelocation.getResourceDomain(), tunkatedPath);
        ResourceLocation resourcelocation1 = this.completeResourceLocation(resourcelocation, 0);

        //garbage vanilla code, copy pasted because there's no proper hooks for this
        try {

            IResource iresource = man.getResource(resourcelocation1);
            BufferedImage[] abufferedimage = new BufferedImage[1 + mipmap];
            abufferedimage[0] = ImageIO.read(iresource.getInputStream());
            TextureMetadataSection texturemetadatasection = (TextureMetadataSection) iresource.getMetadata("texture");

            if(texturemetadatasection != null) {
                List list = texturemetadatasection.getListMipmaps();
                int l;

                if(!list.isEmpty()) {
                    int k = abufferedimage[0].getWidth();
                    l = abufferedimage[0].getHeight();

                    if(MathHelper.roundUpToPowerOfTwo(k) != k || MathHelper.roundUpToPowerOfTwo(l) != l) {
                        throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                    }
                }

                Iterator iterator3 = list.iterator();

                while(iterator3.hasNext()) {
                    l = ((Integer) iterator3.next()).intValue();

                    if(l > 0 && l < abufferedimage.length - 1 && abufferedimage[l] == null) {
                        ResourceLocation resourcelocation2 = this.completeResourceLocation(resourcelocation, l);

                        try {
                            abufferedimage[l] = ImageIO.read(man.getResource(resourcelocation2).getInputStream());
                        } catch(IOException ioexception) {
                            MainRegistry.logger.error("Unable to load miplevel {} from: {}", new Object[] { Integer.valueOf(l), resourcelocation2, ioexception });
                        }
                    }
                }
            }

            AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection) iresource.getMetadata("animation");
            loadSprite(abufferedimage, animationmetadatasection, (float) anisotropic > 1.0F);
        } catch(RuntimeException runtimeexception) {
            cpw.mods.fml.client.FMLClientHandler.instance().trackBrokenTexture(resourcelocation1, runtimeexception.getMessage());
            return true; //return TRUE to prevent stitching non-existent texture, vanilla loading will deal with that!
        } catch(IOException ioexception1) {
            cpw.mods.fml.client.FMLClientHandler.instance().trackMissingTexture(resourcelocation1);
            return true;
        }

        return false; //FALSE! prevents vanilla loading (we just did that ourselves)
    }

    //whatever the fuck this is
    private ResourceLocation completeResourceLocation(ResourceLocation loc, int mipmap) {
        return mipmap == 0 ? new ResourceLocation(loc.getResourceDomain(), String.format("%s/%s%s", new Object[] { this.basePath, loc.getResourcePath(), ".png" }))
                : new ResourceLocation(loc.getResourceDomain(), String.format("%s/mipmaps/%s.%d%s", new Object[] { this.basePath, loc.getResourcePath(), Integer.valueOf(mipmap), ".png" }));
    }*/
}
