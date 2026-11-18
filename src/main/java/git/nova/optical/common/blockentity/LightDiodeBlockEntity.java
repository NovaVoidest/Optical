package git.nova.optical.common.blockentity;

import git.nova.optical.init.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class LightDiodeBlockEntity extends AbstractOpticalLightBlockEntity {

    public LightDiodeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LIGHT_DIODE.get(), pos, state);
    }
}
