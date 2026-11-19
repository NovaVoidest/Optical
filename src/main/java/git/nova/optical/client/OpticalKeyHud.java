package git.nova.optical.client;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.blockentity.AbstractOpticalLightBlockEntity;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import git.nova.optical.common.light.LightColor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Locale;

@EventBusSubscriber(modid = OpticalMod.MODID, value = Dist.CLIENT)
public final class OpticalKeyHud {

    private OpticalKeyHud() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null
                || minecraft.level == null
                || minecraft.screen != null
                || !OpticalKeyController.isHoldingKey(player)) {
            return;
        }

        AbstractOpticalLightBlockEntity light = OpticalKeyController.getHudLight();
        if (light == null) {
            return;
        }

        MutableComponent message = Component.empty();
        if (light instanceof SpotlightBlockEntity spotlight) {
            boolean adjusting = OpticalKeyController.isAdjusting();
            float size = adjusting ? OpticalKeyController.getPendingSize() : spotlight.getSizeDegrees();
            float distance = adjusting ? OpticalKeyController.getPendingDistance() : spotlight.getDistance();
            float rotation = adjusting ? OpticalKeyController.getPendingRotation() : spotlight.getRotationDegrees();
            float brightness = adjusting
                    ? OpticalKeyController.getPendingBrightness()
                    : spotlight.getBrightness();

            message = message
                    .append(value(OpticalKeyController.Parameter.SIZE, "hud.optical.size", format(size, "°")))
                    .append(Component.literal("   ").withStyle(ChatFormatting.GRAY))
                    .append(value(OpticalKeyController.Parameter.DISTANCE, "hud.optical.distance", format(distance, "")))
                    .append(Component.literal("   ").withStyle(ChatFormatting.GRAY))
                    .append(value(OpticalKeyController.Parameter.ROTATION, "hud.optical.rotation", format(rotation, "°")))
                    .append(Component.literal("   ").withStyle(ChatFormatting.GRAY))
                    .append(value(OpticalKeyController.Parameter.BRIGHTNESS, "hud.optical.brightness",
                            percent(brightness)))
                    .append(Component.literal("   ").withStyle(ChatFormatting.GRAY));
        }

        message = message.append(color(light.getTint()));
        player.displayClientMessage(message, true);
    }

    private static Component value(OpticalKeyController.Parameter parameter, String translationKey, String value) {
        boolean selected = OpticalKeyController.getSelected() == parameter;
        return Component.translatable(translationKey).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": " + value)
                        .withStyle(selected ? ChatFormatting.WHITE : ChatFormatting.GRAY));
    }

    private static Component color(int tint) {
        DyeColor dye = LightColor.fromTint(tint);
        Component value = dye == null
                ? Component.translatable("hud.optical.color.none")
                : Component.translatable("color.minecraft." + dye.getName());
        return Component.translatable("hud.optical.color").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(value.copy().withStyle(style -> style.withColor(dye == null ? 0xFFFFFF : tint)));
    }

    private static String format(float value, String suffix) {
        return String.format(Locale.ROOT, "%.1f%s", value, suffix);
    }

    private static String percent(float value) {
        return Math.round(value * 100.0F) + "%";
    }
}
