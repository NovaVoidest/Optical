package git.nova.optical.client;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Locale;

/**
 * Shows the Optical Key readout in the actionbar. The style is kept close to vanilla: plain white
 * values with grey labels, no title and no bold text. The selected value is the only white one,
 * which is enough to show which parameter the mouse will adjust.
 */
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

        SpotlightBlockEntity spotlight = OpticalKeyController.getHudTarget();
        if (spotlight == null) {
            return;
        }

        boolean adjusting = OpticalKeyController.isAdjusting();
        float size = adjusting ? OpticalKeyController.getPendingSize() : spotlight.getSizeDegrees();
        float distance = adjusting ? OpticalKeyController.getPendingDistance() : spotlight.getDistance();
        float rotation = adjusting ? OpticalKeyController.getPendingRotation() : spotlight.getRotationDegrees();

        Component message = Component.empty()
                .append(value(OpticalKeyController.Parameter.SIZE, "hud.optical.size", format(size, "°")))
                .append(Component.literal("   ").withStyle(ChatFormatting.GRAY))
                .append(value(OpticalKeyController.Parameter.DISTANCE, "hud.optical.distance", format(distance, "")))
                .append(Component.literal("   ").withStyle(ChatFormatting.GRAY))
                .append(value(OpticalKeyController.Parameter.ROTATION, "hud.optical.rotation", format(rotation, "°")));

        player.displayClientMessage(message, true);
    }

    private static Component value(OpticalKeyController.Parameter parameter, String translationKey, String value) {
        boolean selected = OpticalKeyController.getSelected() == parameter;
        return Component.translatable(translationKey).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": " + value)
                        .withStyle(selected ? ChatFormatting.WHITE : ChatFormatting.GRAY));
    }

    private static String format(float value, String suffix) {
        return String.format(Locale.ROOT, "%.1f%s", value, suffix);
    }
}
