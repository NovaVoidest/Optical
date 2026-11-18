package git.nova.optical.datagen;

import git.nova.optical.init.blocks.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class OpticalRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public OpticalRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.LIGHT_PROJECTOR.get())
                .pattern("cGc")
                .pattern("cBc")
                .pattern("ccc")
                .define('c', Items.COPPER_INGOT)
                .define('G', Items.GLASS_PANE)
                .define('B', Items.COPPER_BULB)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(recipeOutput);
    }
}
