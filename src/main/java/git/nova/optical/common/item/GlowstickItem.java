package git.nova.optical.common.item;

import git.nova.optical.init.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;

public class GlowstickItem extends Item {

    public static final long GLOW_TIME_MS = 60_000L;
    public static final long FADE_TIME_MS = 30_000L;
    public static final long LIFETIME_MS = GLOW_TIME_MS + FADE_TIME_MS;

    private final DyeColor color;
    private final int lightColor;
    private final int barColor;
    private final float lightRed;
    private final float lightGreen;
    private final float lightBlue;

    public GlowstickItem(Properties properties, DyeColor color, int barColor, int lightColor) {
        super(properties);
        this.color = color;
        this.lightColor = lightColor;
        this.barColor = barColor;
        this.lightRed = (lightColor >> 16 & 0xFF) / 255.0F;
        this.lightGreen = (lightColor >> 8 & 0xFF) / 255.0F;
        this.lightBlue = (lightColor & 0xFF) / 255.0F;
    }

    public float getLightRed() {
        return this.lightRed;
    }

    public float getLightGreen() {
        return this.lightGreen;
    }

    public float getLightBlue() {
        return this.lightBlue;
    }

    public static boolean isCracked(ItemStack stack) {
        return stack.has(ModDataComponents.GLOWSTICK_LIT_AT.get());
    }

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
        return this.barColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip,
                                TooltipFlag flag) {
        MutableComponent colorName = Component.translatable("color.minecraft." + this.color.getName())
                .withStyle(style -> style.withColor(this.lightColor));
        tooltip.add(Component.translatable("tooltip.optical.glowstick.color").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(colorName));
        long elapsed = getElapsedMs(stack);
        if (elapsed >= 0L && elapsed < LIFETIME_MS) {
            long remainingSeconds = (LIFETIME_MS - elapsed + 999L) / 1000L;
            tooltip.add(Component.translatable("tooltip.optical.glowstick.lit", remainingSeconds)
                    .withStyle(style -> style.withColor(this.lightColor)));
        }
    }
}
