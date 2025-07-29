package ha;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    public static boolean unColored = false;
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }
    @Override
    public void init(FMLInitializationEvent event) {
        ClientEventHandler handler = new ClientEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);
        super.init(event);
    }
}
