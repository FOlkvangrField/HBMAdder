package HA.Blocks;

import com.hbm.lib.RefStrings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class ForgeFluid extends BlockFluidClassic {
    @SideOnly(Side.CLIENT)
    public static IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon flowingIcon;
    public String IconName;
    public ForgeFluid(Fluid fluid, Material material,String IconName) {
        super(fluid, material);
        this.IconName = IconName;
        setCreativeTab(null);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return (side == 0 || side == 1) ? stillIcon : flowingIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        stillIcon = register.registerIcon(RefStrings.MODID + ":" + IconName);
        flowingIcon = register.registerIcon(RefStrings.MODID + ":" + IconName);
    }
}
