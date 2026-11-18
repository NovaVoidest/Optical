package git.nova.optical.client;

import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Tiny, client-only bridge used by block entities. Keeping the Veil imports behind this class
 * means dedicated servers never load the rendering code.
 */
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
