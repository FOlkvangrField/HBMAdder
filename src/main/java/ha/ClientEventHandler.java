package ha;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ha.FluidAdder.ForgeFluidIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;

import static ha.FluidAdder.ForgeFluidAdder.*;
import static ha.FluidAdder.ForgeFluidIcon.*;

public class ClientEventHandler {

    @SubscribeEvent
    public void updateTextureSheet(final TextureStitchEvent.Pre ev) {
        if(ev.map.getTextureType()==0){
            stillIconMap.clear();
            flowIconMap.clear();
            for(Fluid fluid : forgeFluids){
                ForgeFluidIcon.FluidIcon Icon = ForgeFluidIcon.getFluidIcon(ev.map, fluid.getName());
                stillIconMap.put(fluid.getName(),Icon.stillIcon);
                flowIconMap.put(fluid.getName(),Icon.flowingIcon);
            }
        }
    }

}
