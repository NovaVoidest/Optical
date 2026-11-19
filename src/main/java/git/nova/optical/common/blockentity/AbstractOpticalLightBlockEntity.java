package git.nova.optical.common.blockentity;

import git.nova.optical.client.ClientLightHooks;
import git.nova.optical.common.light.LightColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public abstract class AbstractOpticalLightBlockEntity extends BlockEntity {

    private boolean manualOn;
    private int tint = LightColor.NONE;

    protected AbstractOpticalLightBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isManualOn() {
        return this.manualOn;
    }

    public int getTint() {
        return this.tint;
    }

    public void setTint(int tint) {
        if (this.tint == tint) {
            return;
        }
        this.tint = tint;
        this.setChanged();
        this.sync();
    }

    public void setManualOn(boolean manualOn) {
        if (this.manualOn == manualOn) {
            return;
        }
        this.manualOn = manualOn;
        this.setChanged();
        this.sync();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level.isClientSide) {
            ClientLightHooks.onBlockEntityAdded(this);
        }
    }

    @Override
    public void setRemoved() {
        if (this.level != null && this.level.isClientSide) {
            ClientLightHooks.onBlockEntityRemoved(this);
        }
        super.setRemoved();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (this.level != null && this.level.isClientSide) {
            ClientLightHooks.onBlockEntityAdded(this);
        }
    }

    protected final void sync() {
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("manual_on", this.manualOn);
        if (this.tint != LightColor.NONE) {
            tag.putInt("tint", this.tint);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.manualOn = tag.getBoolean("manual_on");
        this.tint = tag.contains("tint") ? tag.getInt("tint") & 0xFFFFFF : LightColor.NONE;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("manual_on", this.manualOn);
        if (this.tint != LightColor.NONE) {
            tag.putInt("tint", this.tint);
        }
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
