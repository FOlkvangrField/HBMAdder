package HA.Fluiddder;

import HA.Blocks.ForgeFluid;
import com.hbm.blocks.fluid.ToxicBlock;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.RefStrings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class NewForgeFluid extends Fluid {
    private final FluidType hbmFluid = Fluids.fromName(fluidName.toUpperCase());

    public NewForgeFluid(String fluidName) {
        super(fluidName);
        //new Textures.BlockIcons.CustomIcon
    }
    /*@Override
    @SideOnly(Side.CLIENT)
    public int getColor(){
        return hbmFluid.getColor();
    }*/
    /*@Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return getStillIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getStillIcon() {
        IIcon icon = ForgeFluidIcon.getIcon(hbmFluid);
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getFlowingIcon() {
        IIcon icon = ForgeFluidIcon.getIcon(hbmFluid);
        return icon;
    }*/
}
