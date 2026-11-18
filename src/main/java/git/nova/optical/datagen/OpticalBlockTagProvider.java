package git.nova.optical.datagen;

import git.nova.optical.OpticalMod;
import git.nova.optical.init.blocks.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class OpticalBlockTagProvider extends BlockTagsProvider {
    public OpticalBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                   @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, OpticalMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SPOTLIGHT.get())
                .add(ModBlocks.LIGHT_DIODE.get());
        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.SPOTLIGHT.get())
                .add(ModBlocks.LIGHT_DIODE.get());
    }
}
