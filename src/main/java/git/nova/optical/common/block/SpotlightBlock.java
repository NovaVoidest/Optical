package git.nova.optical.common.block;

import git.nova.optical.common.blockentity.AbstractOpticalLightBlockEntity;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import git.nova.optical.init.blocks.ModBlockEntities;
import git.nova.optical.init.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

/**
 * A directional light block. Its {@link #FACING} property points away from the block it is
 * attached to, so the base of the projector always rests against that support. The head can
 * then be tilted with the Optical Key to aim the beam.
 */
public class SpotlightBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public SpotlightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(LIT, context.getLevel().hasNeighborSignal(pos))
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                   BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) {
            return;
        }
        boolean manual = level.getBlockEntity(pos) instanceof AbstractOpticalLightBlockEntity be && be.isManualOn();
        boolean lit = manual || level.hasNeighborSignal(pos);
        if (state.getValue(LIT) != lit) {
            level.setBlock(pos, state.setValue(LIT, lit), Block.UPDATE_ALL);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        // The Optical Key handles its own right-click interaction. Do not let the block toggle
        // when the key is held.
        if (stack.is(ModItems.OPTICAL_KEY.get())) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof SpotlightBlockEntity blockEntity)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            boolean manual = !blockEntity.isManualOn();
            blockEntity.setManualOn(manual);
            boolean lit = manual || level.hasNeighborSignal(pos);
            level.setBlock(pos, state.setValue(LIT, lit), Block.UPDATE_ALL);
            level.playSound(null, pos, lit ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF,
                    SoundSource.BLOCKS, 0.7F, 1.2F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpotlightBlockEntity(pos, state);
    }

    /**
     * Blockstate rotations for the projector. The model is authored with its base at the bottom
     * and its head pointing north. Rotating the model's local +Y onto {@link #FACING} keeps the
     * base flat against the face the spotlight was placed on, while the head can then be tilted
     * by the block entity.
     */
    public static int modelRotationX(Direction facing) {
        return switch (facing) {
            case UP -> 0;
            case DOWN -> 180;
            case NORTH -> 90;
            case SOUTH -> 270;
            case EAST, WEST -> 90;
        };
    }

    public static int modelRotationY(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case WEST -> 270;
            default -> 0;
        };
    }
}
