package ha;

import ha.Config.Config;
import ha.Converter.TransferRecipe;
import ha.FluidAdder.Storage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ha.jsonHelper.JsonReads;
import static ha.jsonHelper.creatFile;

public class Loader {
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String forgeFluids = "HAForgeFluids", Recipes = "HARecipes";

    public static void setFolder(File folder) {
        jsonHelper.setFolder(folder);
    }

    public static void loadHbmFluidFromJson(Boolean first) {
        if (Config.alwaysRefreshFluid && !Config.needRefreshFluid) Config.set(Config.aFluid, false);
        String json = JsonReads(forgeFluids);
        if (json == null || Config.needRefreshFluid) {
            //json =
            creatFile(forgeFluids, gson.toJson(Storage.hbm_sample()));
            if (Config.needRefreshFluid) Config.needRefreshFluid = false;
            if (first) loadHbmFluidFromJson(false);
            return;
        }
        Storage.hbmModel[] models = gson.fromJson(json, Storage.hbmModel[].class);

        List<Storage.hbmModel> list = new ArrayList<>();
        boolean needRefresh = false;

        if (Config.allcustomMode) list = Arrays.asList(models);
        else for (Storage.hbmModel model : models) {
            if (com.hbm.inventory.fluid.Fluids.fromName(model.name.toUpperCase()) != null) list.add(model);
            else needRefresh = true;
        }
        if (!Config.allcustomMode && needRefresh) creatFile(forgeFluids, gson.toJson(list.toArray()));
        Storage.hbmStorage.addAll((list));
    }
    public static void loadRecipeFromJson(boolean first) {
        if (Config.alwaysRefreshRecipe && !Config.needRefreshRecipe) Config.set(Config.nRecipe, true);
        String json = JsonReads(Recipes);
        if (json == null || Config.needRefreshRecipe) {
            creatFile(Recipes, gson.toJson(TransferRecipe.sample()));
            if (Config.needRefreshRecipe) Config.needRefreshRecipe = false;
            if (first) {
                ClientProxy.unColored = true;
                loadRecipeFromJson(false);
            }
            return;
        }
        TransferRecipe.RecipeContainer[] recipes = gson.fromJson(json, TransferRecipe.RecipeContainer[].class);

        List<TransferRecipe.RecipeContainer> list = new ArrayList<>();
        boolean needRefresh = false;

        if (Config.allcustomMode) list = Arrays.asList(recipes);
        else for (TransferRecipe.RecipeContainer recipe : recipes) {
            if (recipe.getInput() != null) list.add(recipe);
            else needRefresh = true;
        }
        if (!Config.allcustomMode && needRefresh) creatFile(Recipes, gson.toJson(list.toArray()));
        TransferRecipe.storage.addAll((list));
    }
}
