package git.nova.optical.init.items;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.item.GlowstickItem;
import git.nova.optical.common.item.OpticalKeyItem;
import git.nova.optical.init.blocks.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(OpticalMod.MODID);

    public static final DeferredItem<OpticalKeyItem> OPTICAL_KEY = ITEMS.register("optical_key",
            () -> new OpticalKeyItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<GlowstickItem> GLOWSTICK = ITEMS.register("glowstick",
            () -> new GlowstickItem(new Item.Properties().stacksTo(1)));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> OPTICAL_TAB =
            OpticalMod.CREATIVE_MODE_TABS.register("optical", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.optical"))
                    .icon(() -> new ItemStack(OPTICAL_KEY.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.SPOTLIGHT.get());
                        output.accept(ModBlocks.LIGHT_DIODE.get());
                        output.accept(OPTICAL_KEY.get());
                        output.accept(GLOWSTICK.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        OpticalMod.CREATIVE_MODE_TABS.register(eventBus);
    }
}
