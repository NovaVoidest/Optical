package git.nova.optical.datagen;

import git.nova.optical.init.blocks.ModBlocks;
import git.nova.optical.init.items.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class OpticalRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public OpticalRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.SPOTLIGHT.get())
                .pattern("cGc")
                .pattern("cBc")
                .pattern("ccc")
                .define('c', Items.COPPER_INGOT)
                .define('G', Items.GLASS_PANE)
                .define('B', Items.COPPER_BULB)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.LIGHT_DIODE.get())
                .pattern(" c ")
                .pattern("cGc")
                .pattern(" c ")
                .define('c', Items.COPPER_INGOT)
                .define('G', Items.GLOWSTONE_DUST)
                .unlockedBy("has_copper", has(Items.COPPER_INGOT))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.OPTICAL_KEY.get())
                .requires(Items.COPPER_INGOT)
                .requires(Items.IRON_INGOT)
                .requires(Items.AMETHYST_SHARD)
                .unlockedBy("has_amethyst", has(Items.AMETHYST_SHARD))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.GLOWSTICK.get())
                .requires(Items.GLOW_INK_SAC)
                .requires(Tags.Items.GLASS_PANES)
                .unlockedBy("has_glow_ink_sac", has(Items.GLOW_INK_SAC))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.GLOWSTICK_BLUE.get())
                .requires(Items.GLOW_INK_SAC)
                .requires(Tags.Items.GLASS_PANES)
                .requires(Items.BLUE_DYE)
                .unlockedBy("has_glow_ink_sac", has(Items.GLOW_INK_SAC))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.GLOWSTICK_PURPLE.get())
                .requires(Items.GLOW_INK_SAC)
                .requires(Tags.Items.GLASS_PANES)
                .requires(Items.PURPLE_DYE)
                .unlockedBy("has_glow_ink_sac", has(Items.GLOW_INK_SAC))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.GLOWSTICK_GREEN.get())
                .requires(Items.GLOW_INK_SAC)
                .requires(Tags.Items.GLASS_PANES)
                .requires(Items.GREEN_DYE)
                .unlockedBy("has_glow_ink_sac", has(Items.GLOW_INK_SAC))
                .save(recipeOutput);
    }
}
