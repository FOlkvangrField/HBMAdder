package ha;

import ha.Converter.TransferRecipe;
import ha.FluidAdder.Storage;
import ventivu.core.Core.Commands.AbstractAutoRegister;
import ventivu.core.Core.Commands.IReloadable;
import ventivu.core.Core.Commands.IVersionProvider;

import static ha.Loader.loadFluidFromJson;
import static ha.Loader.loadRecipeFromJson;

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
