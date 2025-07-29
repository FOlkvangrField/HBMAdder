package ha.FluidAdder;

import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;

import java.util.ArrayList;
import java.util.List;

import static ha.FluidAdder.ForgeFluidAdder.hbmFluidsType;

public class Storage {
    public static final List<hbmModel> hbmStorage = new ArrayList<>();

    public static hbmModel[] hbm_sample() {
        List<hbmModel> list = new ArrayList<>();
        for(FluidType type : hbmFluidsType){
            String forgeName = type.getName().toLowerCase();
            if(type!=Fluids.NONE&&type!=Fluids.SCHRABIDIC&&type!=Fluids.WATZ&&type!=Fluids.SULFURIC_ACID){
                list.add(new hbmModel(forgeName,type.temperature,false));
            }
        }
        return list.toArray(new hbmModel[0]);
    }
    public static class hbmModel {
        public String name;
        public int temperature;
        public boolean hasBlock;
        public hbmModel(String name, int temperature, boolean hasBlock) {
            this.name = name;
            this.temperature = temperature;
            this.hasBlock = hasBlock;
        }
    }
}