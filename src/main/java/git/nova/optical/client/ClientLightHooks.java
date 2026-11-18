package git.nova.optical.client;

import net.minecraft.world.level.block.entity.BlockEntity;

public final class ClientLightHooks {

    private ClientLightHooks() {
    }

    public static void onBlockEntityAdded(BlockEntity blockEntity) {
        ClientLightManager.track(blockEntity);
    }

    public static void onBlockEntityRemoved(BlockEntity blockEntity) {
        ClientLightManager.untrack(blockEntity);
    }
}
