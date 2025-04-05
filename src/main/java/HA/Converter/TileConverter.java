package HA.Converter;

import api.hbm.fluid.*;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.lib.Library;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.fauxpointtwelve.DirPos;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.*;

public class TileConverter extends TileEntityLoadedBase implements IBufPacketReceiver,IFluidHandler, IFluidStandardTransceiver{
    static final int[] speed = new int[]{100, 500, 1000, 3000, 5000, 8000, 10000, -1};
    static final int[] capacity = new int[]{1000, 2000, 4000, 6000, 10000, 20000, 50000, Integer.MAX_VALUE};
    public short mode = 0;
    FluidTank forgefluidtank;
    com.hbm.inventory.fluid.tank.FluidTank hbmfluidtank;
    int age = 0;
    public void switchMode() {
        mode = (short) (mode == 0 ? 1 : 0);
    }
    public TileConverter(int level) {
        if (level > 7) level = 7;
        forgefluidtank = new FluidTank(capacity[level]);
        hbmfluidtank = new com.hbm.inventory.fluid.tank.FluidTank(Fluids.NONE, capacity[level]);
    }
    public TileConverter() {
        this(0); // 调用原有构造函数并设置默认等级
    }
    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return forgefluidtank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if(resource != null && canDrain(from, resource.getFluid()))
        {
            return forgefluidtank.drain(resource.amount, doDrain);
        }
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if(canDrain(from, null))
        {
            return forgefluidtank.drain(maxDrain, doDrain);
        }
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return (forgefluidtank.getFluid() == null || forgefluidtank.getFluid().getFluid() == fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if(forgefluidtank != null)
        {
            if(fluid == null || forgefluidtank.getFluid() != null && forgefluidtank.getFluid().getFluid() == fluid)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{forgefluidtank.getInfo()};
    }
    @Override
    public void serialize(ByteBuf buf){
        buf.writeShort(this.mode);
        hbmfluidtank.serialize(buf);
    }
    @Override
    public void deserialize(ByteBuf buf){
        this.mode = buf.readShort();
        hbmfluidtank.deserialize(buf);
    }
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        mode = nbt.getShort("mode");
        NBTTagCompound inputTank = nbt.getCompoundTag("Input");
        NBTTagCompound outputTank = nbt.getCompoundTag("Output");
        forgefluidtank.readFromNBT(inputTank);
        hbmfluidtank.readFromNBT(outputTank, "output");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("mode", mode);
        NBTTagCompound inputTank = new NBTTagCompound();
        NBTTagCompound outputTank = new NBTTagCompound();
        forgefluidtank.writeToNBT(inputTank);
        hbmfluidtank.writeToNBT(outputTank, "output");
        nbt.setTag("Input", inputTank);
        nbt.setTag("Output", outputTank);
    }

    @Override
    public void updateEntity() {
        if(!worldObj.isRemote) {
            int a = getBlockMetadata();
            age = age >= 20 ? 0 : age + 1;
            unsubscribeToAllAround(hbmfluidtank.getTankType(), this);
            if (age == 0) {
                switch (this.mode) {
                    case 0://forge->hbm
                        TileEntity tile = worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
                        if (tile instanceof TileEntityPipeBaseNT) {
                            hbmfluidtank.setTankType(((TileEntityPipeBaseNT) tile).getType());
                        }
                        if (forgefluidtank.getFluid() != null) {
                            FluidType outputFluid = TransferRecipe.recipeMap.get(forgefluidtank.getFluid().getFluid());
                            if (outputFluid != null && (hbmfluidtank.getTankType() == Fluids.NONE || outputFluid == hbmfluidtank.getTankType())) {
                                if (a != 7) {//非创造模式
                                    int vir = forgefluidtank.drain(speed[a], false).amount;
                                    if (hbmfluidtank.getTankType() == Fluids.NONE)
                                        hbmfluidtank.setTankType(outputFluid);
                                    int free = hbmfluidtank.getMaxFill() - hbmfluidtank.getFill();
                                    forgefluidtank.drain(Math.min(vir, free), true);
                                    hbmfluidtank.setFill(hbmfluidtank.getFill() + Math.min(vir, free));
                                    //this.output.updateTank(xCoord, yCoord, zCoord, worldObj.provider.dimensionId);
                                } else {
                                    if (hbmfluidtank.getTankType() == Fluids.NONE)
                                        hbmfluidtank.setTankType(outputFluid);
                                    hbmfluidtank.setFill(hbmfluidtank.getFill() + forgefluidtank.drain(Math.min(forgefluidtank.getFluidAmount(), hbmfluidtank.getMaxFill() - hbmfluidtank.getFill()), true).amount);
                                }
                            }
                        }
                        //HBM新版本取消了sendFluidtoAll方法，使用修改后的输送流体方法
                        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {//查找可用输出方向
                            //sendfFluid参数为容器流体种类、流体压力、转换器实体位置和方向
                            this.sendFluid(hbmfluidtank, this.getWorldObj(), this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ, dir);
                        }
                        break;
                    case 1://hbm->forge
                        tile = worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
                        if (tile instanceof TileEntityPipeBaseNT) {
                            hbmfluidtank.setTankType(((TileEntityPipeBaseNT) tile).getType());
                        }
                        //output.setFill(transmitFluidFairly(worldObj, output, this, output.getFill(), this.mode == 1, this.mode == 0, getConPos()));
                        for (DirPos pos : getConPos()) {
                            this.trySubscribe(hbmfluidtank.getTankType(), worldObj, pos);
                        }
                        if (hbmfluidtank.getTankType() != Fluids.NONE && hbmfluidtank.getFill() > 0) {
                            Fluid outputFluid = null;
                            Map<Fluid, FluidType> fluidMap = TransferRecipe.recipeMap;
                            for (Map.Entry<Fluid, FluidType> entry : fluidMap.entrySet()) {
                                if (entry.getValue().equals(hbmfluidtank.getTankType())) {
                                    outputFluid = entry.getKey();
                                    break;
                                }
                            }
                            if (outputFluid != null) {
                                if (a != 7) {
                                    int vir = speed[a];
                                    int free = forgefluidtank.getCapacity() - forgefluidtank.getFluidAmount();
                                    //int free = output.getMaxFill() - output.getFill();
                                    if (forgefluidtank.getFluid() == null || !(forgefluidtank.getFluid().getFluid() != outputFluid && forgefluidtank.getFluidAmount() > 0)) {
                                        int transformFluidAmount = Math.min(Math.min(vir, free), hbmfluidtank.getFill());
                                        FluidStack fluidStack = new FluidStack(outputFluid, forgefluidtank.getFluidAmount() + transformFluidAmount);
                                        forgefluidtank.fill(fluidStack, true);
                                        //input.drain(Math.min(vir, free), true);
                                        hbmfluidtank.setFill(hbmfluidtank.getFill() - transformFluidAmount);
                                    }

                                } else {
                                    if (forgefluidtank.getFluid() == null || !(forgefluidtank.getFluid().getFluid() != outputFluid && forgefluidtank.getFluidAmount() > 0)) {
                                        FluidStack fluidStack = new FluidStack(outputFluid, forgefluidtank.getFluidAmount() + Math.min(hbmfluidtank.getFill(), forgefluidtank.getCapacity() - forgefluidtank.getFluidAmount()));
                                        forgefluidtank.fill(fluidStack, true);
                                        hbmfluidtank.setFill(hbmfluidtank.getFill() > forgefluidtank.getCapacity() - forgefluidtank.getFluidAmount() ? hbmfluidtank.getFill() - forgefluidtank.getCapacity() + forgefluidtank.getFluidAmount() : 0);
                                    }
                                }
                            }
                        }

                        break;
                }
            }
            networkPackNT(25);
            //if (this.mode == 0 && hbmfluidtank.getFill() == 0 && hbmfluidtank.getTankType() != Fluids.NONE) hbmfluidtank.setTankType(Fluids.NONE);
        }
    }

    //////////////////////////////////////////Forge End/////////////////////////////////////
    @Override
    public com.hbm.inventory.fluid.tank.FluidTank[] getSendingTanks() {
        return mode == 0 ? new com.hbm.inventory.fluid.tank.FluidTank[]{hbmfluidtank} : new com.hbm.inventory.fluid.tank.FluidTank[0];
    }

    @Override
    public com.hbm.inventory.fluid.tank.FluidTank[] getReceivingTanks() {
        //return new com.hbm.inventory.fluid.tank.FluidTank[]{output};
        return mode == 0 ? new com.hbm.inventory.fluid.tank.FluidTank[0] : new com.hbm.inventory.fluid.tank.FluidTank[]{hbmfluidtank};
    }
    protected DirPos[] getConPos() {
        return new DirPos[] {
                new DirPos(xCoord + 1, yCoord, zCoord, Library.POS_X),
                new DirPos(xCoord - 1, yCoord, zCoord, Library.NEG_X),
                new DirPos(xCoord, yCoord + 1, zCoord, Library.POS_Y),
                new DirPos(xCoord, yCoord - 1, zCoord, Library.NEG_Y),
                new DirPos(xCoord, yCoord, zCoord + 1, Library.POS_Z),
                new DirPos(xCoord, yCoord, zCoord - 1, Library.NEG_Z)
        };
    }
    /*protected static int transmitFluidFairly(World world, com.hbm.inventory.fluid.tank.FluidTank tank, IFluidConnector that, int fill, boolean connect, boolean send, DirPos[] connections) {

        Set<IPipeNet> nets = new HashSet<>();
        Set<IFluidConnector> consumers = new HashSet<>();
        FluidType type = tank.getTankType();
        int pressure = tank.getPressure();

        for(DirPos pos : connections) {

            TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

            if(te instanceof IFluidConductor) {
                IFluidConductor con = (IFluidConductor) te;
                if(con.getPipeNet(type) != null) {
                    nets.add(con.getPipeNet(type));
                    con.getPipeNet(type).unsubscribe(that);
                    consumers.addAll(con.getPipeNet(type).getSubscribers());
                }

                //if it's just a consumer, buffer it as a subscriber
            } else if(te instanceof IFluidConnector) {
                consumers.add((IFluidConnector) te);
            }
        }

        consumers.remove(that);

        if(fill > 0 && send) {
            List<IFluidConnector> con = new ArrayList<>();
            con.addAll(consumers);

            con.removeIf(x -> x == null || !(x instanceof TileEntity) || ((TileEntity)x).isInvalid());

            if(PipeNet.trackingInstances == null) {
                PipeNet.trackingInstances = new ArrayList<>();
            }

            PipeNet.trackingInstances.clear();
            nets.forEach(x -> {
                if(x instanceof PipeNet) PipeNet.trackingInstances.add((PipeNet) x);
            });

            fill = (int) PipeNet.fairTransfer(con, type, pressure, fill);
        }

        //resubscribe to buffered nets, if necessary
        if(connect) {
            nets.forEach(x -> x.subscribe(that));
        }

        return fill;
    }*/
    @Override
    public com.hbm.inventory.fluid.tank.FluidTank[] getAllTanks() {
        return new com.hbm.inventory.fluid.tank.FluidTank[]{hbmfluidtank};
    }


    @Override
    public boolean isLoaded() {
        return true;
    }
}
