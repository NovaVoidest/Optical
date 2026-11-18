package git.nova.optical.common.blockentity;

import git.nova.optical.init.blocks.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class SpotlightBlockEntity extends AbstractOpticalLightBlockEntity {

    public static final float DEFAULT_SIZE = 25.0F;
    public static final float MIN_SIZE = 5.0F;
    public static final float MAX_SIZE = 80.0F;

    public static final float DEFAULT_DISTANCE = 25.0F;
    public static final float MIN_DISTANCE = 4.0F;
    public static final float MAX_DISTANCE = 64.0F;

    public static final float DEFAULT_ROTATION = 0.0F;
    public static final float MIN_ROTATION = -90.0F;
    public static final float MAX_ROTATION = 90.0F;

    private float sizeDegrees = DEFAULT_SIZE;
    private float distance = DEFAULT_DISTANCE;
    private float rotationDegrees = DEFAULT_ROTATION;

    public SpotlightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPOTLIGHT.get(), pos, state);
    }

    public float getSizeDegrees() {
        return this.sizeDegrees;
    }

    public float getDistance() {
        return this.distance;
    }

    public float getRotationDegrees() {
        return this.rotationDegrees;
    }

    /**
     * Server-side configuration change, sent by the Optical Key.
     */
    public void setConfiguration(float sizeDegrees, float distance, float rotationDegrees) {
        float clampedSize = Mth.clamp(sizeDegrees, MIN_SIZE, MAX_SIZE);
        float clampedDistance = Mth.clamp(distance, MIN_DISTANCE, MAX_DISTANCE);
        float clampedRotation = Mth.clamp(rotationDegrees, MIN_ROTATION, MAX_ROTATION);
        if (this.sizeDegrees == clampedSize
                && this.distance == clampedDistance
                && this.rotationDegrees == clampedRotation) {
            return;
        }
        this.sizeDegrees = clampedSize;
        this.distance = clampedDistance;
        this.rotationDegrees = clampedRotation;
        this.setChanged();
        this.sync();
    }

    /**
     * Immediate client-side update so the light and the head react while the mouse is moving.
     * The authoritative values still arrive from the server.
     */
    public void setClientConfiguration(float sizeDegrees, float distance, float rotationDegrees) {
        this.sizeDegrees = Mth.clamp(sizeDegrees, MIN_SIZE, MAX_SIZE);
        this.distance = Mth.clamp(distance, MIN_DISTANCE, MAX_DISTANCE);
        this.rotationDegrees = Mth.clamp(rotationDegrees, MIN_ROTATION, MAX_ROTATION);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("size", this.sizeDegrees);
        tag.putFloat("distance", this.distance);
        tag.putFloat("rotation", this.rotationDegrees);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.sizeDegrees = Mth.clamp(tag.contains("size") ? tag.getFloat("size") : DEFAULT_SIZE,
                MIN_SIZE, MAX_SIZE);
        this.distance = Mth.clamp(tag.contains("distance") ? tag.getFloat("distance") : DEFAULT_DISTANCE,
                MIN_DISTANCE, MAX_DISTANCE);
        this.rotationDegrees = Mth.clamp(
                tag.contains("rotation") ? tag.getFloat("rotation") : DEFAULT_ROTATION,
                MIN_ROTATION, MAX_ROTATION);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putFloat("size", this.sizeDegrees);
        tag.putFloat("distance", this.distance);
        tag.putFloat("rotation", this.rotationDegrees);
        return tag;
    }
}
