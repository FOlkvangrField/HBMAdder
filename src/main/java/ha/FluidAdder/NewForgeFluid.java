package ha.FluidAdder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public class NewForgeFluid extends Fluid {
    public boolean hasBlock;
    public NewForgeFluid(String fluidName,boolean hasBlock) {
        super(fluidName);
        this.hasBlock = hasBlock;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon() {
        return getStillIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getStillIcon() {
        IIcon icon = ForgeFluidIcon.stillIconMap.get(fluidName);//ForgeFluidBlocks.stillIcon;
        return icon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getFlowingIcon() {
        IIcon icon = ForgeFluidIcon.flowIconMap.get(fluidName);//ForgeFluidBlocks.flowingIcon;
        return icon;
    }
}
