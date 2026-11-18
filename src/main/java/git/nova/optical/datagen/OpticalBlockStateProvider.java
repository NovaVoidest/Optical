package git.nova.optical.datagen;
import git.nova.optical.OpticalMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class OpticalBlockStateProvider extends BlockStateProvider {
    public OpticalBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, OpticalMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Example for a standard 6-sided cube block:
        // simpleBlockWithItem(ModBlocks.OPTICAL_GLASS.get(), cubeAll(ModBlocks.OPTICAL_GLASS.get()));
    }
}