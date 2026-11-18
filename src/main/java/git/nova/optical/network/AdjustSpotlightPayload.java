package git.nova.optical.network;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import git.nova.optical.init.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent by the Optical Key whenever the player changes a spotlight value.
 */
public record AdjustSpotlightPayload(BlockPos pos, float sizeDegrees, float distance, float rotationDegrees)
        implements CustomPacketPayload {

    public static final Type<AdjustSpotlightPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(OpticalMod.MODID, "adjust_spotlight"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AdjustSpotlightPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, AdjustSpotlightPayload::pos,
                    ByteBufCodecs.FLOAT, AdjustSpotlightPayload::sizeDegrees,
                    ByteBufCodecs.FLOAT, AdjustSpotlightPayload::distance,
                    ByteBufCodecs.FLOAT, AdjustSpotlightPayload::rotationDegrees,
                    AdjustSpotlightPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AdjustSpotlightPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        if (!mainHand.is(ModItems.OPTICAL_KEY.get()) && !offHand.is(ModItems.OPTICAL_KEY.get())) {
            return;
        }

        if (player.distanceToSqr(Vec3.atCenterOf(payload.pos())) > 64.0D) {
            return;
        }

        if (player.level().getBlockEntity(payload.pos()) instanceof SpotlightBlockEntity spotlight) {
            spotlight.setConfiguration(payload.sizeDegrees(), payload.distance(), payload.rotationDegrees());
        }
    }
}
