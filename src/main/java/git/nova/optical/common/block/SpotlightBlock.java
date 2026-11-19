package git.nova.optical.common.block;

import git.nova.optical.common.blockentity.AbstractOpticalLightBlockEntity;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import git.nova.optical.common.light.LightColor;
import git.nova.optical.common.light.LightTintInteraction;
import git.nova.optical.init.ModSounds;
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
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class SpotlightBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);

    private static final VoxelShape[] SHAPES = createShapes();

    public SpotlightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(ROTATION, 0)
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROTATION, LIT, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).ordinal() * 4 + state.getValue(ROTATION)];
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(ROTATION, placementRotation(context, facing))
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
            if (lit) {
                level.playSound(null, pos, ModSounds.SPOTLIGHT_ON.get(), SoundSource.BLOCKS, 0.7F, 1.0F);
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemInteractionResult pane = LightTintInteraction.applyPane(level, pos, player, hand);
        if (pane != null) {
            return pane;
        }
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

        if (player.isShiftKeyDown() && blockEntity.getTint() != LightColor.NONE) {
            return LightTintInteraction.removePane(level, pos);
        }

        if (!level.isClientSide) {
            boolean manual = !blockEntity.isManualOn();
            blockEntity.setManualOn(manual);
            boolean lit = manual || level.hasNeighborSignal(pos);
            level.setBlock(pos, state.setValue(LIT, lit), Block.UPDATE_ALL);
            level.playSound(null, pos,
                    lit ? ModSounds.SPOTLIGHT_ON.get() : SoundEvents.COPPER_BULB_TURN_OFF,
                    SoundSource.BLOCKS, 0.7F, 1.2F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpotlightBlockEntity(pos, state);
    }

    private static int placementRotation(BlockPlaceContext context, Direction facing) {
        if (!facing.getAxis().isVertical()) {
            return 0;
        }
        int roll = switch (context.getHorizontalDirection()) {
            case SOUTH -> 2;
            case WEST -> 1;
            case NORTH -> 0;
            case EAST -> 3;
            default -> 0;
        };
        return facing == Direction.DOWN ? (roll + 2) & 3 : roll;
    }

    public static int modelRotationX(Direction facing, int rotation) {
        return switch (facing) {
            case UP -> 0;
            case DOWN -> 180;
            case SOUTH -> effectiveRoll(facing, rotation) == 0 ? 270 : 90;
            case NORTH, EAST, WEST -> effectiveRoll(facing, rotation) == 0 ? 90 : 270;
        };
    }

    public static int modelRotationY(Direction facing, int rotation) {
        return switch (facing) {
            case UP -> (360 - rotation * 90) % 360;
            case DOWN -> (rotation * 90) % 360;
            case NORTH, SOUTH -> effectiveRoll(facing, rotation) == 0 ? 0 : 180;
            case EAST -> effectiveRoll(facing, rotation) == 0 ? 90 : 270;
            case WEST -> effectiveRoll(facing, rotation) == 0 ? 270 : 90;
        };
    }

    public static int effectiveRoll(Direction facing, int rotation) {
        return facing.getAxis().isVertical() ? rotation & 3 : rotation & 2;
    }

    private static VoxelShape[] createShapes() {
        VoxelShape[] shapes = new VoxelShape[Direction.values().length * 4];
        for (Direction facing : Direction.values()) {
            for (int rotation = 0; rotation < 4; rotation++) {
                shapes[facing.ordinal() * 4 + rotation] = rotateBox(1, 0, 2, 15, 14, 14, facing, rotation);
            }
        }
        return shapes;
    }

    private static VoxelShape rotateBox(double minX, double minY, double minZ,
                                        double maxX, double maxY, double maxZ,
                                        Direction facing, int rotation) {
        double[] min = rotatePoint(minX, minY, minZ, facing, rotation);
        double[] max = rotatePoint(maxX, maxY, maxZ, facing, rotation);
        return Shapes.box(
                Math.min(min[0], max[0]) / 16.0,
                Math.min(min[1], max[1]) / 16.0,
                Math.min(min[2], max[2]) / 16.0,
                Math.max(min[0], max[0]) / 16.0,
                Math.max(min[1], max[1]) / 16.0,
                Math.max(min[2], max[2]) / 16.0);
    }

    private static double[] rotatePoint(double x, double y, double z, Direction facing, int rotation) {
        int rotationX = modelRotationX(facing, rotation);
        int rotationY = modelRotationY(facing, rotation);
        double centeredX = x - 8.0;
        double centeredY = y - 8.0;
        double centeredZ = z - 8.0;

        double rotatedY;
        double rotatedZ;
        switch (rotationX) {
            case 90 -> {
                rotatedY = centeredZ;
                rotatedZ = -centeredY;
            }
            case 180 -> {
                rotatedY = -centeredY;
                rotatedZ = -centeredZ;
            }
            case 270 -> {
                rotatedY = -centeredZ;
                rotatedZ = centeredY;
            }
            default -> {
                rotatedY = centeredY;
                rotatedZ = centeredZ;
            }
        }

        double rotatedX;
        switch (rotationY) {
            case 90 -> {
                rotatedX = -rotatedZ;
                rotatedZ = centeredX;
            }
            case 180 -> {
                rotatedX = -centeredX;
                rotatedZ = -rotatedZ;
            }
            case 270 -> {
                rotatedX = rotatedZ;
                rotatedZ = -centeredX;
            }
            default -> rotatedX = centeredX;
        }

        return new double[]{rotatedX + 8.0, rotatedY + 8.0, rotatedZ + 8.0};
    }
}
