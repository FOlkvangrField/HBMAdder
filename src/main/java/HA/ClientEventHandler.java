package HA;

import HA.Converter.ConverterBlock;
import HA.Fluiddder.ForgeFluidIcon;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.render.icon.RGBMutatorInterpolatedComponentRemap;
import com.hbm.render.icon.TextureAtlasSpriteMutatable;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static HA.Fluiddder.ForgeFluidAdder.forgeFluids;
//import static HA.Fluiddder.ForgeFluidIcon.createColoredIcon;

public class ClientEventHandler {
    private static final ResourceLocation GRAY_TEXTURE = new ResourceLocation("HA:textures/blocks/custom_water.png");
    @SubscribeEvent()
    public void textureStich(TextureStitchEvent.Pre event){



    }
    @SubscribeEvent
    public void updateTextureSheet(final TextureStitchEvent.Pre ev) {
        if(ev.map.getTextureType()==0){
            for(Fluid fluid : forgeFluids){
                //TextureAtlasSpriteMutatable icon = new TextureAtlasSpriteMutatable("HA:custom_water-" + fluid.getName(),new RGBMutatorInterpolatedComponentRemap(0xFFFFFF, 0x505050, fluid.getColor(), fluid.getColor()));
                //ev.map.setTextureEntry("HA:custom_water-" + fluid.getName(),icon);
                fluid.setIcons(ConverterBlock.iconMap.get(fluid.getName()),ConverterBlock.iconMap.get(fluid.getName()));
                //IIcon icon = createColoredIcon("custom_water", Fluids.fromName(fluid.getName().toUpperCase()).getColor(), (TextureAtlasSprite) event.map.registerIcon("HA:custom_water"));
                //IIcon icon = getColoredIcon(Fluids.fromName(fluid.getName().toUpperCase()).getColor());
                //fluid.setIcons(event.map.registerIcon("HA:custom_water"),event.map.registerIcon("HA:custom_water"));
                //fluid.setIcons(event.map.registerIcon("hbm:gui/fluids/custom_water"),event.map.registerIcon("hbm:gui/fluids/custom_water"));
            }
        }
        /*if (ev.map.getTextureType() == 0) {
            ForgeFluidIcon.getAll().forEach(p -> p.register(ev.map));
            for(Fluid fluid : forgeFluids){
                IIcon icon = ForgeFluidIcon.getIcon(Fluids.fromName(fluid.getName().toUpperCase()));
                fluid.setIcons(icon,icon);
            }
        }*/

    }

}
