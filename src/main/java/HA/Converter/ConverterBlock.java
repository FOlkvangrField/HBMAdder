package HA.Converter;

import HA.HBMAddon;
import api.hbm.block.IToolable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.trait.FluidTraitSimple;
import com.hbm.render.icon.RGBMutatorInterpolatedComponentRemap;
import com.hbm.render.icon.TextureAtlasSpriteMutatable;
import com.hbm.util.I18nUtil;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hbm.inventory.fluid.Fluids;

import static HA.Fluiddder.ForgeFluidIcon.ColorEdit;

public class ConverterBlock extends Block implements IToolable, ITooltipProvider, ILookOverlay  {
    IIcon[] top = new IIcon[8], side = new IIcon[8], down = new IIcon[8];
    public static HashMap<String, IIcon> iconMap = new HashMap();

    public ConverterBlock() {
        super(Material.iron);
        this.setBlockName("converter");
        GameRegistry.registerBlock(this, ConverterBlockItem.class, "converter");
        GameRegistry.registerTileEntity(TileConverter.class, "Converter");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public void registerBlockIcons(IIconRegister register) {
        for (int i = 0; i < top.length; i++) {
            top[i] = register.registerIcon(HBMAddon.MODID + ":transfer_top." + i);
            side[i] = register.registerIcon(HBMAddon.MODID + ":transfer_side." + i);
            down[i] = register.registerIcon(HBMAddon.MODID + ":transfer_down." + i);
        }
        if(register instanceof TextureMap) {
            TextureMap map = (TextureMap) register;
            for (FluidType fluid : Fluids.getAll()) {
                if (fluid != Fluids.NONE) {
                    RGBMutatorInterpolatedComponentRemap iconColor = new RGBMutatorInterpolatedComponentRemap(0xFFFFFF, 0x505050, ColorEdit(fluid.getColor(),1.5), ColorEdit(fluid.getColor(),0.5));
                    String name = "HA:custom_water-";
                    TextureAtlasSpriteMutatable icon = new TextureAtlasSpriteMutatable( name+ fluid.getName(), iconColor).setBlockAtlas();
                    if(fluid.hasTrait(FluidTraitSimple.FT_Viscous.class)){
                        name = "HA:custom_oil-";
                        icon = new TextureAtlasSpriteMutatable(name + fluid.getName(), iconColor).setBlockAtlas();
                    }
                    if(fluid.temperature>1000||fluid.hasTrait(FluidTraitSimple.FT_Amat.class)||fluid.hasTrait(FluidTraitSimple.FT_Plasma.class)){
                        name = "HA:custom_lava-";
                        icon = new TextureAtlasSpriteMutatable(name + fluid.getName(), iconColor).setBlockAtlas();
                    }
                    map.setTextureEntry(name + fluid.getName(), icon);
                    iconMap.put(fluid.getName().toLowerCase(),icon);
                }
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        switch (ForgeDirection.getOrientation(side)) {
            case DOWN:
                return down[meta];
            case UP:
                return top[meta];
            default:
                return this.side[meta];
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
        list.add(new ItemStack(item, 1, 4));
        list.add(new ItemStack(item, 1, 5));
        list.add(new ItemStack(item, 1, 6));
        list.add(new ItemStack(item, 1, 7));
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileConverter(metadata);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileConverter tile = (TileConverter) world.getTileEntity(x, y, z);
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null) {
            if (!world.isRemote) {
                return handleRightClick(tile, ForgeDirection.getOrientation(side), player, true, false);
            } else return FluidContainerRegistry.isContainer(current);
        }
        return false;
    }
    @Override
    public boolean onScrew(World world, EntityPlayer player, int x, int y, int z, int side, float fX, float fY, float fZ, IToolable.ToolType tool) {
        if(tool != IToolable.ToolType.SCREWDRIVER){
            return false;
        }
        if(world.isRemote) return true;
        if(!world.isRemote){

        }
        TileConverter te = (TileConverter) world.getTileEntity(x, y, z);
        te.switchMode();
        te.markDirty();
        return true;


    }
    public static boolean handleRightClick(IFluidHandler tank, ForgeDirection side, EntityPlayer player, boolean fill, boolean drain) {
        if (player == null || tank == null) return false;
        ItemStack current = player.inventory.getCurrentItem();
        if (current != null) {
            FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(current);
            if (fill && liquid != null) {
                int used = tank.fill(side, liquid, true);
                if (used > 0) {
                    if (!player.capabilities.isCreativeMode) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(current));
                        player.inventory.markDirty();
                    }
                    return true;
                }
            } else if (drain) {
                FluidStack available = tank.drain(side, 2147483647, false);
                if (available != null) {
                    ItemStack filled = FluidContainerRegistry.fillFluidContainer(available, current);
                    liquid = FluidContainerRegistry.getFluidForFilledItem(filled);
                    if (liquid != null) {
                        if (current.stackSize > 1) {
                            if (!player.inventory.addItemStackToInventory(filled)) return false;
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(current));
                            player.inventory.markDirty();
                        } else {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, consumeItem(current));
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, filled);
                            player.inventory.markDirty();
                        }
                        tank.drain(side, liquid.amount, true);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ItemStack consumeItem(ItemStack stack) {
        if (stack.stackSize == 1) {
            if (stack.getItem().hasContainerItem(stack)) return stack.getItem().getContainerItem(stack);
            return null;
        }
        stack.splitStack(1);
        return stack;
    }
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean ext) {
        list.add(EnumChatFormatting.GOLD + "使用螺丝刀右键改变转换模式");
    }

    @Override
    public void printHook(RenderGameOverlayEvent.Pre event, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

        if(!(te instanceof TileConverter))
            return;

        TileConverter converter = (TileConverter) te;
        String forgeFluid = null;
        String hbmFluid = converter.hbmfluidtank.getTankType().getLocalizedName();
        Map<Fluid, FluidType> fluidMap = TransferRecipe.recipeMap;
        for (Map.Entry<Fluid, FluidType> entry : fluidMap.entrySet()) {
            if (entry.getValue().equals(converter.hbmfluidtank.getTankType())) {
                forgeFluid = entry.getKey().getLocalizedName();
                break;
            }
        }
        List<String> text = new ArrayList();
        switch(converter.mode){
            case 0:
                text.add("Forge: "+forgeFluid+"->Hbm: "+hbmFluid);
                break;
            case 1:
                text.add("Hbm: "+hbmFluid+" -> Forge: "+forgeFluid);
                break;
        }
        ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getUnlocalizedName() + ".name"), 0xffff00, 0x404000, text);
    }
}
