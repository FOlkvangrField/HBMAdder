package ha;

import ha.FluidAdder.FluidColor;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    public static boolean unColored = false;
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        //ForgeFluidIcon.collect();
    }
    @Override
    public void init(FMLInitializationEvent event) {
        ClientEventHandler handler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);
        super.init(event);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(FluidColor.instance);
        if (unColored) FluidColor.setAllColor();
    }
}
