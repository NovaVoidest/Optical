package git.nova.optical.client;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import git.nova.optical.init.items.ModItems;
import git.nova.optical.network.AdjustSpotlightPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = OpticalMod.MODID, value = Dist.CLIENT)
public final class OpticalKeyController {

    public enum Parameter {
        SIZE("hud.optical.size"),
        DISTANCE("hud.optical.distance"),
        ROTATION("hud.optical.rotation");

        private final String translationKey;

        Parameter(String translationKey) {
            this.translationKey = translationKey;
        }

        public Component displayName() {
            return Component.translatable(this.translationKey);
        }
    }

    private static final double PIXELS_PER_STEP = 8.0D;
    private static final float SIZE_STEP = 1.0F;
    private static final float DISTANCE_STEP = 1.0F;
    private static final float ROTATION_STEP = 1.0F;
    private static final float FINE_MULTIPLIER = 0.1F;

    private static Parameter selected = Parameter.SIZE;
    private static boolean adjusting;
    @Nullable
    private static BlockPos targetPos;
    private static float pendingSize = SpotlightBlockEntity.DEFAULT_SIZE;
    private static float pendingDistance = SpotlightBlockEntity.DEFAULT_DISTANCE;
    private static float pendingRotation = SpotlightBlockEntity.DEFAULT_ROTATION;
    private static double scrollAccumulator;

    private OpticalKeyController() {
    }

    public static Parameter getSelected() {
        return selected;
    }

    public static boolean isAdjusting() {
        return adjusting;
    }

    public static float getPendingSize() {
        return pendingSize;
    }

    public static float getPendingDistance() {
        return pendingDistance;
    }

    public static float getPendingRotation() {
        return pendingRotation;
    }

    public static boolean isHoldingKey(@Nullable Player player) {
        return player != null
                && (player.getMainHandItem().is(ModItems.OPTICAL_KEY.get())
                || player.getOffhandItem().is(ModItems.OPTICAL_KEY.get()));
    }

    @Nullable
    public static SpotlightBlockEntity getTargetSpotlight() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        if (minecraft.hitResult instanceof BlockHitResult hitResult
                && minecraft.level.getBlockEntity(hitResult.getBlockPos()) instanceof SpotlightBlockEntity spotlight) {
            return spotlight;
        }
        return null;
    }

    @Nullable
    public static SpotlightBlockEntity getHudTarget() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        if (adjusting && targetPos != null
                && minecraft.level.getBlockEntity(targetPos) instanceof SpotlightBlockEntity spotlight) {
            return spotlight;
        }
        return getTargetSpotlight();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null || !isHoldingKey(minecraft.player)) {
            stopAdjusting();
            return;
        }

        boolean useDown = minecraft.options.keyUse.isDown()
                && minecraft.screen == null
                && minecraft.mouseHandler.isMouseGrabbed();
        SpotlightBlockEntity target = getTargetSpotlight();

        if (useDown && target != null) {
            if (!adjusting || targetPos == null || !targetPos.equals(target.getBlockPos())) {
                startAdjusting(target);
            }
        } else if (adjusting) {
            stopAdjusting();
        }
    }

    @SubscribeEvent
    public static void onInteraction(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isAttack()) {
            if (trySelectParameter()) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        } else if (event.isUseItem() && adjusting) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    private static boolean trySelectParameter() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !isHoldingKey(minecraft.player) || getTargetSpotlight() == null) {
            return false;
        }

        Parameter[] values = Parameter.values();
        selected = values[(selected.ordinal() + 1) % values.length];
        if (adjusting && targetPos != null
                && minecraft.level != null
                && minecraft.level.getBlockEntity(targetPos) instanceof SpotlightBlockEntity spotlight) {
            pendingSize = spotlight.getSizeDegrees();
            pendingDistance = spotlight.getDistance();
            pendingRotation = spotlight.getRotationDegrees();
        }
        return true;
    }

    private static void startAdjusting(SpotlightBlockEntity spotlight) {
        adjusting = true;
        targetPos = spotlight.getBlockPos();
        pendingSize = spotlight.getSizeDegrees();
        pendingDistance = spotlight.getDistance();
        pendingRotation = spotlight.getRotationDegrees();
        scrollAccumulator = 0.0D;
    }

    private static void stopAdjusting() {
        adjusting = false;
        targetPos = null;
        scrollAccumulator = 0.0D;
    }

    public static boolean handleMouseDelta(double deltaX, double deltaY) {
        if (!adjusting) {
            return false;
        }

        scrollAccumulator += deltaX;
        int steps = (int) (scrollAccumulator / PIXELS_PER_STEP);
        if (steps != 0) {
            scrollAccumulator -= steps * PIXELS_PER_STEP;
            adjust(steps);
        }
        return true;
    }

    private static void adjust(int steps) {
        if (targetPos == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null
                || !(minecraft.level.getBlockEntity(targetPos) instanceof SpotlightBlockEntity spotlight)) {
            stopAdjusting();
            return;
        }

        float fine = minecraft.options.keyShift.isDown() ? FINE_MULTIPLIER : 1.0F;
        switch (selected) {
            case SIZE -> {
                float next = Mth.clamp(
                        pendingSize + steps * SIZE_STEP * fine,
                        SpotlightBlockEntity.MIN_SIZE,
                        SpotlightBlockEntity.MAX_SIZE
                );
                if (next == pendingSize) {
                    return;
                }
                pendingSize = next;
            }
            case DISTANCE -> {
                float next = Mth.clamp(
                        pendingDistance + steps * DISTANCE_STEP * fine,
                        SpotlightBlockEntity.MIN_DISTANCE,
                        SpotlightBlockEntity.MAX_DISTANCE
                );
                if (next == pendingDistance) {
                    return;
                }
                pendingDistance = next;
            }
            case ROTATION -> {
                float next = Mth.clamp(
                        pendingRotation + steps * ROTATION_STEP * fine,
                        SpotlightBlockEntity.MIN_ROTATION,
                        SpotlightBlockEntity.MAX_ROTATION
                );
                if (next == pendingRotation) {
                    return;
                }
                pendingRotation = next;
            }
        }

        spotlight.setClientConfiguration(pendingSize, pendingDistance, pendingRotation);
        PacketDistributor.sendToServer(
                new AdjustSpotlightPayload(targetPos, pendingSize, pendingDistance, pendingRotation));
    }
}
