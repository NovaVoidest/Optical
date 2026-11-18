package git.nova.optical.datagen;

import git.nova.optical.OpticalMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class OpticalBlockTagProvider extends BlockTagsProvider {
    public OpticalBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, OpticalMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Example:
        // tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.OPTICAL_GLASS.get());
        // tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.OPTICAL_GLASS.get());
    }
}