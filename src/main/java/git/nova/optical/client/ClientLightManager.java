package git.nova.optical.client;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.data.SpotLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import git.nova.optical.OpticalMod;
import git.nova.optical.common.block.LightDiodeBlock;
import git.nova.optical.common.block.SpotlightBlock;
import git.nova.optical.common.blockentity.LightDiodeBlockEntity;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import git.nova.optical.common.item.GlowstickItem;
import git.nova.optical.common.light.LightColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

@EventBusSubscriber(modid = OpticalMod.MODID, value = Dist.CLIENT)
public final class ClientLightManager {

    private static final Vector3f LOCAL_FORWARD = new Vector3f(0.0F, 0.0F, 1.0F);

    private static final float SPOTLIGHT_BRIGHTNESS = 5.0F;
    private static final float SPOTLIGHT_ANGLE_RADIANS = (float) Math.toRadians(1.5D);

    private static final float DIODE_BRIGHTNESS = 1.5F;
    private static final float DIODE_RADIUS = 15.0F;
    private static final float DIODE_INSCATTERING = 1.5F;
    private static final float DIODE_RED = 1.0F;
    private static final float DIODE_GREEN = 246.0F / 255.0F;
    private static final float DIODE_BLUE = 213.0F / 255.0F;

    private static final float GLOWSTICK_BRIGHTNESS = 1.6F;
    private static final float GLOWSTICK_RADIUS = 12.0F;
    private static final float GLOWSTICK_INSCATTERING = 1.2F;
    private static final float GLOWSTICK_RED = 0.25F;
    private static final float GLOWSTICK_GREEN = 1.0F;
    private static final float GLOWSTICK_BLUE = 0.3F;

    private static final float DIODE_FLICKER_AMOUNT = 0.04F;
    private static final float GLOWSTICK_FLICKER_AMOUNT = 0.05F;

    private static final Map<BlockEntity, TrackedLight> TRACKED = new IdentityHashMap<>();

    private static PointLightData glowstickLight;
    private static LightRenderHandle<PointLightData> glowstickHandle;

    private ClientLightManager() {
    }

    public static void track(BlockEntity blockEntity) {
        TRACKED.computeIfAbsent(blockEntity, ignored -> new TrackedLight());
    }

    public static void untrack(BlockEntity blockEntity) {
        TrackedLight light = TRACKED.remove(blockEntity);
        if (light != null) {
            light.free();
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            clearAll();
            return;
        }

        LightRenderer renderer = lightRenderer();
        if (renderer == null) {
            return;
        }

        Iterator<Map.Entry<BlockEntity, TrackedLight>> iterator = TRACKED.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockEntity, TrackedLight> entry = iterator.next();
            BlockEntity blockEntity = entry.getKey();
            if (blockEntity.isRemoved()
                    || blockEntity.getLevel() != level
                    || !level.isLoaded(blockEntity.getBlockPos())) {
                entry.getValue().free();
                iterator.remove();
                continue;
            }
            entry.getValue().update(blockEntity, renderer);
        }
    }

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        LightRenderer renderer = lightRenderer();
        if (renderer == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        updateGlowstick(minecraft, renderer, partialTick);
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            clearAll();
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        clearAll();
    }

    private static void clearAll() {
        for (TrackedLight light : TRACKED.values()) {
            light.free();
        }
        TRACKED.clear();
        freeGlowstick();
    }

    private static void updateGlowstick(Minecraft minecraft, LightRenderer renderer, float partialTick) {
        LocalPlayer player = minecraft.player;
        ItemStack stack = player == null ? ItemStack.EMPTY : findLitGlowstick(player);
        float strength = stack.isEmpty() ? 0.0F : GlowstickItem.getGlowStrength(stack);
        if (strength <= 0.0F) {
            freeGlowstick();
            return;
        }

        if (glowstickHandle == null || !glowstickHandle.isValid()) {
            if (glowstickLight == null) {
                glowstickLight = new PointLightData();
            }
            glowstickHandle = renderer.addLight(glowstickLight);
        }

        long gameTime = minecraft.level != null ? minecraft.level.getGameTime() : 0L;
        float flicker = flicker(gameTime + partialTick, GLOWSTICK_FLICKER_AMOUNT);

        Vec3 position = player.getPosition(partialTick);
        glowstickLight.setPosition(position.x, position.y + 1.05D, position.z);
        glowstickLight.setBrightness(GLOWSTICK_BRIGHTNESS * strength * flicker);
        glowstickLight.setColor(GLOWSTICK_RED, GLOWSTICK_GREEN, GLOWSTICK_BLUE);
        glowstickLight.setRadius(GLOWSTICK_RADIUS);
        glowstickLight.setOcclusionEnabled(true);
        glowstickLight.setInscatteringStrength(GLOWSTICK_INSCATTERING * strength);
    }

    private static ItemStack findLitGlowstick(LocalPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof GlowstickItem && GlowstickItem.isLit(mainHand)) {
            return mainHand;
        }
        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof GlowstickItem && GlowstickItem.isLit(offHand)) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }

    private static float flicker(float gameTime, float amount) {
        double t = gameTime * 0.37D;
        double noise = Math.sin(t) * 0.5D
                + Math.sin(t * 2.13D + 1.7D) * 0.3D
                + Math.sin(t * 4.71D + 3.9D) * 0.2D;
        return 1.0F + (float) noise * amount;
    }

    private static void freeGlowstick() {
        if (glowstickHandle != null) {
            glowstickHandle.free();
            glowstickHandle = null;
        }
        glowstickLight = null;
    }

    @Nullable
    private static LightRenderer lightRenderer() {
        VeilRenderer renderer = VeilRenderSystem.renderer();
        return renderer != null ? renderer.getLightRenderer() : null;
    }

    private static final class TrackedLight {

        private SpotLightData spotData;
        private LightRenderHandle<SpotLightData> spotHandle;
        private PointLightData pointData;
        private LightRenderHandle<PointLightData> pointHandle;

        private void update(BlockEntity blockEntity, LightRenderer renderer) {
            BlockState state = blockEntity.getBlockState();

            if (blockEntity instanceof SpotlightBlockEntity spotlight) {
                freePoint();
                updateSpotlight(spotlight, state, renderer);
            } else if (blockEntity instanceof LightDiodeBlockEntity diode) {
                freeSpot();
                updateDiode(diode, state, renderer);
            }
        }

        private void updateSpotlight(SpotlightBlockEntity spotlight, BlockState state, LightRenderer renderer) {
            if (!state.hasProperty(SpotlightBlock.LIT) || !state.getValue(SpotlightBlock.LIT)) {
                freeSpot();
                return;
            }

            if (this.spotHandle == null || !this.spotHandle.isValid()) {
                if (this.spotData == null) {
                    this.spotData = new SpotLightData();
                }
                this.spotHandle = renderer.addLight(this.spotData);
            }

            Direction facing = state.getValue(SpotlightBlock.FACING);
            int rotation = state.getValue(SpotlightBlock.ROTATION);
            float tilt = spotlight.getRotationDegrees();
            BlockPos pos = spotlight.getBlockPos();

            Vector3f origin = SpotlightBeamGeometry.lensPosition(facing, rotation, tilt);
            Vector3f beam = SpotlightBeamGeometry.beamDirection(facing, rotation, tilt);

            this.spotData.getPositionMutable().set(
                    pos.getX() + origin.x,
                    pos.getY() + origin.y,
                    pos.getZ() + origin.z
            );
            this.spotData.getOrientationMutable().rotationTo(beam, LOCAL_FORWARD);
            this.spotData.setSize((float) Math.toRadians(spotlight.getSizeDegrees()));
            this.spotData.setDistance(spotlight.getDistance());
            this.spotData.setBrightness(SPOTLIGHT_BRIGHTNESS * spotlight.getBrightness());
            int tint = spotlight.getTint();
            if (tint == LightColor.NONE) {
                this.spotData.setColor(1.0F, 1.0F, 1.0F);
            } else {
                this.spotData.setColor(LightColor.red(tint), LightColor.green(tint), LightColor.blue(tint));
            }
            this.spotData.setAngle(SPOTLIGHT_ANGLE_RADIANS);
            this.spotData.setOcclusionEnabled(true);
            this.spotData.setInscatteringStrength(0.0F);
        }

        private void updateDiode(LightDiodeBlockEntity diode, BlockState state, LightRenderer renderer) {
            if (!state.hasProperty(LightDiodeBlock.LIT) || !state.getValue(LightDiodeBlock.LIT)) {
                freePoint();
                return;
            }

            if (this.pointHandle == null || !this.pointHandle.isValid()) {
                if (this.pointData == null) {
                    this.pointData = new PointLightData();
                }
                this.pointHandle = renderer.addLight(this.pointData);
            }

            long gameTime = diode.getLevel() != null ? diode.getLevel().getGameTime() : 0L;
            float flicker = flicker(gameTime, DIODE_FLICKER_AMOUNT);

            Vec3 center = Vec3.atCenterOf(diode.getBlockPos());
            this.pointData.setPosition(center.x, center.y, center.z);
            this.pointData.setBrightness(DIODE_BRIGHTNESS * flicker);
            int tint = diode.getTint();
            if (tint == LightColor.NONE) {
                this.pointData.setColor(DIODE_RED, DIODE_GREEN, DIODE_BLUE);
            } else {
                this.pointData.setColor(LightColor.red(tint), LightColor.green(tint), LightColor.blue(tint));
            }
            this.pointData.setRadius(DIODE_RADIUS);
            this.pointData.setOcclusionEnabled(true);
            this.pointData.setInscatteringStrength(DIODE_INSCATTERING);
        }

        private void free() {
            freeSpot();
            freePoint();
        }

        private void freeSpot() {
            if (this.spotHandle != null) {
                this.spotHandle.free();
                this.spotHandle = null;
            }
            this.spotData = null;
        }

        private void freePoint() {
            if (this.pointHandle != null) {
                this.pointHandle.free();
                this.pointHandle = null;
            }
            this.pointData = null;
        }
    }
}
