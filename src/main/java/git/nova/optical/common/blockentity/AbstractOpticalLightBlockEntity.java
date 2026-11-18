package git.nova.optical.common.blockentity;

import git.nova.optical.client.ClientLightHooks;
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

/**
 * Shared logic for all light-emitting blocks. The complete light is created on the client by
 * {@link ClientLightHooks}; only the small configuration needed to rebuild it is synced here.
 */
public abstract class AbstractOpticalLightBlockEntity extends BlockEntity {

    private boolean manualOn;

    protected AbstractOpticalLightBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isManualOn() {
        return this.manualOn;
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
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.manualOn = tag.getBoolean("manual_on");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("manual_on", this.manualOn);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
