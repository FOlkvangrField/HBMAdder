package HA;

import HA.Converter.TransferRecipe;
import HA.Fluiddder.FluidAdder;
import HA.Fluiddder.Storage;
import cpw.mods.fml.common.Optional;
import ventivu.core.Core.Commands.AbstractAutoRegister;
import ventivu.core.Core.Commands.IReloadable;
import ventivu.core.Core.Commands.IVersionProvider;

import static HA.Loader.loadFluidFromJson;
import static HA.Loader.loadRecipeFromJson;

public class VersionProvider extends AbstractAutoRegister implements IVersionProvider, IReloadable {
    @Override
    public String version() {
        return HBMAddon.VERSION;
    }

    @Override
    public String name() {
        return HBMAddon.MODNAME;
    }
    @Override
    //@Optional.Method(modid = "magcore")
    public void reload() {
        Storage.storage.clear();
        TransferRecipe.rollBack();
        loadFluidFromJson(true);
        loadRecipeFromJson(true);
        //FluidAdder.reBuild();
        TransferRecipe.Construct();
    }
}
