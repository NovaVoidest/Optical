package git.nova.optical.init.items;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.item.GlowstickItem;
import git.nova.optical.common.item.OpticalKeyItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(OpticalMod.MODID);

    public static final DeferredItem<OpticalKeyItem> OPTICAL_KEY = ITEMS.register("optical_key",
            () -> new OpticalKeyItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<GlowstickItem> GLOWSTICK = ITEMS.register("glowstick",
            () -> new GlowstickItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
