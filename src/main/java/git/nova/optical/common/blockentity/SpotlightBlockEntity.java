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

    public static final float DEFAULT_BRIGHTNESS = 1.0F;
    public static final float MIN_BRIGHTNESS = 0.25F;
    public static final float MAX_BRIGHTNESS = 2.0F;

    private float sizeDegrees = DEFAULT_SIZE;
    private float distance = DEFAULT_DISTANCE;
    private float rotationDegrees = DEFAULT_ROTATION;
    private float brightness = DEFAULT_BRIGHTNESS;

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

    public float getBrightness() {
        return this.brightness;
    }

    public void setConfiguration(float sizeDegrees, float distance, float rotationDegrees, float brightness) {
        float clampedSize = Mth.clamp(sizeDegrees, MIN_SIZE, MAX_SIZE);
        float clampedDistance = Mth.clamp(distance, MIN_DISTANCE, MAX_DISTANCE);
        float clampedRotation = Mth.clamp(rotationDegrees, MIN_ROTATION, MAX_ROTATION);
        float clampedBrightness = Mth.clamp(brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
        if (this.sizeDegrees == clampedSize
                && this.distance == clampedDistance
                && this.rotationDegrees == clampedRotation
                && this.brightness == clampedBrightness) {
            return;
        }
        this.sizeDegrees = clampedSize;
        this.distance = clampedDistance;
        this.rotationDegrees = clampedRotation;
        this.brightness = clampedBrightness;
        this.setChanged();
        this.sync();
    }

    public void setClientConfiguration(float sizeDegrees, float distance, float rotationDegrees, float brightness) {
        this.sizeDegrees = Mth.clamp(sizeDegrees, MIN_SIZE, MAX_SIZE);
        this.distance = Mth.clamp(distance, MIN_DISTANCE, MAX_DISTANCE);
        this.rotationDegrees = Mth.clamp(rotationDegrees, MIN_ROTATION, MAX_ROTATION);
        this.brightness = Mth.clamp(brightness, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("size", this.sizeDegrees);
        tag.putFloat("distance", this.distance);
        tag.putFloat("rotation", this.rotationDegrees);
        tag.putFloat("brightness", this.brightness);
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
        this.brightness = Mth.clamp(
                tag.contains("brightness") ? tag.getFloat("brightness") : DEFAULT_BRIGHTNESS,
                MIN_BRIGHTNESS, MAX_BRIGHTNESS);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putFloat("size", this.sizeDegrees);
        tag.putFloat("distance", this.distance);
        tag.putFloat("rotation", this.rotationDegrees);
        tag.putFloat("brightness", this.brightness);
        return tag;
    }
}
