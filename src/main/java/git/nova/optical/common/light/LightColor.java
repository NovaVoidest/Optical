package git.nova.optical.common.light;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;
import java.util.Map;

public final class LightColor {

    public static final int NONE = -1;

    private static final Map<Item, DyeColor> PANES = Map.of(
            Items.RED_STAINED_GLASS_PANE, DyeColor.RED,
            Items.ORANGE_STAINED_GLASS_PANE, DyeColor.ORANGE,
            Items.YELLOW_STAINED_GLASS_PANE, DyeColor.YELLOW,
            Items.LIME_STAINED_GLASS_PANE, DyeColor.LIME,
            Items.CYAN_STAINED_GLASS_PANE, DyeColor.CYAN,
            Items.BLUE_STAINED_GLASS_PANE, DyeColor.BLUE,
            Items.PURPLE_STAINED_GLASS_PANE, DyeColor.PURPLE,
            Items.PINK_STAINED_GLASS_PANE, DyeColor.PINK
    );

    private LightColor() {
    }

    @Nullable
    public static DyeColor fromPane(ItemStack stack) {
        return stack.isEmpty() ? null : PANES.get(stack.getItem());
    }

    @Nullable
    public static DyeColor fromTint(int tint) {
        if (tint == NONE) {
            return null;
        }
        for (DyeColor dye : PANES.values()) {
            if (rgb(dye) == tint) {
                return dye;
            }
        }
        return null;
    }

    public static ItemStack pane(int tint) {
        DyeColor dye = fromTint(tint);
        if (dye == null) {
            return ItemStack.EMPTY;
        }
        for (Map.Entry<Item, DyeColor> entry : PANES.entrySet()) {
            if (entry.getValue() == dye) {
                return new ItemStack(entry.getKey());
            }
        }
        return ItemStack.EMPTY;
    }

    public static int rgb(DyeColor dye) {
        return dye.getTextureDiffuseColor() & 0xFFFFFF;
    }

    public static float red(int tint) {
        return (tint >> 16 & 0xFF) / 255.0F;
    }

    public static float green(int tint) {
        return (tint >> 8 & 0xFF) / 255.0F;
    }

    public static float blue(int tint) {
        return (tint & 0xFF) / 255.0F;
    }
}
