package git.nova.optical.init.items;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.item.GlowstickItem;
import git.nova.optical.common.item.OpticalKeyItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(OpticalMod.MODID);

    public static final DeferredItem<OpticalKeyItem> OPTICAL_KEY = ITEMS.register("optical_key",
            () -> new OpticalKeyItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<GlowstickItem> GLOWSTICK = ITEMS.register("glowstick",
            () -> new GlowstickItem(new Item.Properties().stacksTo(1), DyeColor.CYAN, 0x00FFFF, 0x00FFFF));

    public static final DeferredItem<GlowstickItem> GLOWSTICK_BLUE = ITEMS.register("glowstick_blue",
            () -> new GlowstickItem(new Item.Properties().stacksTo(1), DyeColor.BLUE, 0x2A6BFF, 0x2A6BFF));

    public static final DeferredItem<GlowstickItem> GLOWSTICK_PURPLE = ITEMS.register("glowstick_purple",
            () -> new GlowstickItem(new Item.Properties().stacksTo(1), DyeColor.PURPLE, 0xA64DFF, 0xA64DFF));

    public static final DeferredItem<GlowstickItem> GLOWSTICK_GREEN = ITEMS.register("glowstick_green",
            () -> new GlowstickItem(new Item.Properties().stacksTo(1), DyeColor.GREEN, 0x40FF4D, 0x40FF4D));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
