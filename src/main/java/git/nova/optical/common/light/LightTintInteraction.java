package git.nova.optical.common.light;

import git.nova.optical.common.blockentity.AbstractOpticalLightBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public final class LightTintInteraction {

    private LightTintInteraction() {
    }

    @Nullable
    public static ItemInteractionResult applyPane(Level level, BlockPos pos, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        DyeColor color = LightColor.fromPane(stack);
        if (color == null || !(level.getBlockEntity(pos) instanceof AbstractOpticalLightBlockEntity light)) {
            return null;
        }
        if (!level.isClientSide) {
            int previous = light.getTint();
            if (previous != LightColor.NONE && previous != LightColor.rgb(color)) {
                ItemStack old = LightColor.pane(previous);
                if (!old.isEmpty()) {
                    Block.popResource(level, pos, old);
                }
            }
            light.setTint(LightColor.rgb(color));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 1.1F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public static InteractionResult removePane(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof AbstractOpticalLightBlockEntity light)) {
            return InteractionResult.PASS;
        }
        int tint = light.getTint();
        if (tint == LightColor.NONE) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            light.setTint(LightColor.NONE);
            ItemStack pane = LightColor.pane(tint);
            if (!pane.isEmpty()) {
                Block.popResource(level, pos, pane);
            }
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.6F, 1.1F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
