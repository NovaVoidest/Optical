package git.nova.optical.datagen;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.block.SpotlightBlock;
import git.nova.optical.init.blocks.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class OpticalBlockStateProvider extends BlockStateProvider {

    public OpticalBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, OpticalMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile spotlightOff = models().getExistingFile(modLoc("block/spotlight_holder"));
        ModelFile spotlightOn = models().getExistingFile(modLoc("block/spotlight_holder_on"));

        getVariantBuilder(ModBlocks.SPOTLIGHT.get()).forAllStates(state -> {
            Direction facing = state.getValue(SpotlightBlock.FACING);
            int rotation = state.getValue(SpotlightBlock.ROTATION);
            boolean lit = state.getValue(SpotlightBlock.LIT);
            return ConfiguredModel.builder()
                    .modelFile(lit ? spotlightOn : spotlightOff)
                    .rotationX(SpotlightBlock.modelRotationX(facing, rotation))
                    .rotationY(SpotlightBlock.modelRotationY(facing, rotation))
                    .build();
        });

        ModelFile diode = models().getExistingFile(modLoc("block/light_diode"));
        getVariantBuilder(ModBlocks.LIGHT_DIODE.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(diode)
                .build());
    }
}
