package git.nova.optical.common.item;

import git.nova.optical.init.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * A single-use chemical glowstick.
 * <p>
 * Right clicking cracks the stick, which starts a real-time burn. It glows at full strength for
 * one minute and then fades out over thirty seconds. The item itself is never destroyed, but
 * once the fade completes it is spent and can no longer produce light.
 */
public class GlowstickItem extends Item {

    public static final long GLOW_TIME_MS = 60_000L;
    public static final long FADE_TIME_MS = 30_000L;
    public static final long LIFETIME_MS = GLOW_TIME_MS + FADE_TIME_MS;

    /** Purple, as requested, instead of the usual green-to-red durability colours. */
    public static final int BAR_COLOR = 0x9B4DFF;

    public GlowstickItem(Properties properties) {
        super(properties);
    }

    public static boolean isCracked(ItemStack stack) {
        return stack.has(ModDataComponents.GLOWSTICK_LIT_AT.get());
    }

    /**
     * @return milliseconds since the stick was cracked, or {@code -1} while it is intact.
     */
    public static long getElapsedMs(ItemStack stack) {
        Long litAt = stack.get(ModDataComponents.GLOWSTICK_LIT_AT.get());
        return litAt == null ? -1L : Math.max(0L, System.currentTimeMillis() - litAt);
    }

    public static boolean isLit(ItemStack stack) {
        long elapsed = getElapsedMs(stack);
        return elapsed >= 0L && elapsed < LIFETIME_MS;
    }

    public static boolean isSpent(ItemStack stack) {
        return getElapsedMs(stack) >= LIFETIME_MS;
    }

    /**
     * @return brightness multiplier, 1 while glowing and linearly fading to 0 over the last 30s.
     */
    public static float getGlowStrength(ItemStack stack) {
        long elapsed = getElapsedMs(stack);
        if (elapsed < 0L || elapsed >= LIFETIME_MS) {
            return 0.0F;
        }
        if (elapsed <= GLOW_TIME_MS) {
            return 1.0F;
        }
        return 1.0F - (float) (elapsed - GLOW_TIME_MS) / (float) FADE_TIME_MS;
    }

    /**
     * @return remaining lifetime as a 1..0 fraction, used by the durability-style bar.
     */
    public static float getLifeFraction(ItemStack stack) {
        long elapsed = getElapsedMs(stack);
        if (elapsed < 0L) {
            return 0.0F;
        }
        return Mth.clamp(1.0F - (float) elapsed / (float) LIFETIME_MS, 0.0F, 1.0F);
    }

    private static void crack(ItemStack stack, Level level, Player player) {
        if (isCracked(stack)) {
            return;
        }
        stack.set(ModDataComponents.GLOWSTICK_LIT_AT.get(), System.currentTimeMillis());
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TURTLE_EGG_CRACK, SoundSource.PLAYERS, 1.0F, 1.35F);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (isCracked(stack)) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide) {
            crack(stack, level, player);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        ItemStack stack = context.getItemInHand();
        if (isCracked(stack)) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide) {
            crack(stack, context.getLevel(), player);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isLit(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * getLifeFraction(stack));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
                                TooltipFlag flag) {
        long elapsed = getElapsedMs(stack);
        if (elapsed < 0L) {
            tooltip.add(Component.translatable("tooltip.optical.glowstick.intact")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.optical.glowstick.hint")
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else if (elapsed < LIFETIME_MS) {
            long remainingSeconds = (LIFETIME_MS - elapsed + 999L) / 1000L;
            tooltip.add(Component.translatable("tooltip.optical.glowstick.lit", remainingSeconds)
                    .withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.translatable("tooltip.optical.glowstick.burns")
                    .withStyle(ChatFormatting.DARK_GRAY));
        } else {
            tooltip.add(Component.translatable("tooltip.optical.glowstick.spent")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
