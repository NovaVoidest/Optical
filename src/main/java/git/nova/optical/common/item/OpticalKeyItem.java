package git.nova.optical.common.item;

import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

/**
 * Configuration tool for Optical light blocks.
 * <p>
 * Selection and adjustment are handled client-side by
 * {@code git.nova.optical.client.OpticalKeyController}. This item only prevents the
 * spotlight from toggling while the key is held.
 */
public class OpticalKeyItem extends Item {

    public OpticalKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof SpotlightBlockEntity) {
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
                                TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.optical.optical_key.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.optical.optical_key.2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.optical.optical_key.3").withStyle(ChatFormatting.DARK_GRAY));
    }
}
