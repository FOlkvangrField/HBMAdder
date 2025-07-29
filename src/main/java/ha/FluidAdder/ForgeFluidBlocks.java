package ha.FluidAdder;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.ModDamageSource;
import com.hbm.util.ContaminationUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import java.util.Random;

import static ha.FluidAdder.ForgeFluidIcon.*;


public class ForgeFluidBlocks extends BlockFluidClassic {
    @SideOnly(Side.CLIENT)
    public static IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    public static IIcon flowingIcon;
    public String IconName;
    protected FluidType hbmFluidType;
    protected boolean viscosity = false;
    protected float rad = 0F;
    protected float corrosive = 0F;
    public Random rand = new Random();

    public ForgeFluidBlocks(Fluid fluid, Material material, String IconName, MapColor iMapColor) {
        super(fluid, material);
        this.IconName = IconName;
        this.hbmFluidType = Fluids.fromName(this.IconName.toUpperCase());
        displacements.put(this, false);
        setCreativeTab(null);
    }
    public void setViscosity(boolean viscosity) {
        this.viscosity = viscosity;
    }
    public void setRadiation(float rad) {
        this.rad = rad;
    }
    public void setCorrosive(float corrosive) {
        this.corrosive = corrosive;
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (viscosity && entity != null) {
            entity.setInWeb();
        }
        if (rad > 0 && entity instanceof EntityLivingBase) {
            ContaminationUtil.contaminate((EntityLivingBase)entity, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.CREATIVE, rad);
        }
        if(corrosive > 0 && entity instanceof EntityLivingBase) {
            if(!world.isRemote){
                entity.attackEntityFrom(ModDamageSource.acid, corrosive / 10F);
            }
            if(entity.ticksExisted % 5 == 0) {
                world.playSoundAtEntity(entity, "random.fizz", 0.2F, 1F);
            }
        }
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        stillIcon = stillIconMap.get(fluidName);
        flowingIcon = flowIconMap.get(fluidName);
        return (side == 0 || side == 1) ? stillIcon : flowingIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {}
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        super.updateTick(world, x, y, z, rand);

        if(corrosive > 50){
            reactToBlocks2(world, x + 1, y, z);
            reactToBlocks2(world, x - 1, y, z);
            reactToBlocks2(world, x, y + 1, z);
            reactToBlocks2(world, x, y - 1, z);
            reactToBlocks2(world, x, y, z + 1);
            reactToBlocks2(world, x, y, z - 1);
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);

        if(corrosive > 50){
            reactToBlocks(world, x + 1, y, z);
            reactToBlocks(world, x - 1, y, z);
            reactToBlocks(world, x, y + 1, z);
            reactToBlocks(world, x, y - 1, z);
            reactToBlocks(world, x, y, z + 1);
            reactToBlocks(world, x, y, z - 1);
        }
    }

    public void reactToBlocks(World world, int x, int y, int z) {
        if(world.getBlock(x, y, z) != this) {
            Block block = world.getBlock(x, y, z);

            if(block.getMaterial().isLiquid()) {
                world.setBlock(x, y, z, Blocks.air);
            }
        }
    }

    public void reactToBlocks2(World world, int x, int y, int z) {
        if(world.getBlock(x, y, z) != this) {
            Block block = world.getBlock(x, y, z);

            if (block == Blocks.stone_brick_stairs ||
                    block == Blocks.stonebrick ||
                    block == Blocks.stone_slab ||
                    block == Blocks.stone) {
                if(rand.nextInt(20) == 0)
                    world.setBlock(x, y, z, Blocks.cobblestone);
            } else if (block == Blocks.cobblestone) {
                if(rand.nextInt(15) == 0)
                    world.setBlock(x, y, z, Blocks.gravel);
            } else if (block == Blocks.sandstone) {
                if(rand.nextInt(5) == 0)
                    world.setBlock(x, y, z, Blocks.sand);
            } else if (block == Blocks.hardened_clay ||
                    block == Blocks.stained_hardened_clay) {
                if(rand.nextInt(10) == 0)
                    world.setBlock(x, y, z, Blocks.clay);
            } else if (block.getExplosionResistance(null) < 1.2F) {
                world.setBlock(x, y, z, Blocks.air);
            }
        }
    }
}

