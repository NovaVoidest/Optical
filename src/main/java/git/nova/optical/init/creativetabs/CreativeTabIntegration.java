package git.nova.optical.init.creativetabs;

import git.nova.optical.init.blocks.ModBlocks;
import git.nova.optical.init.items.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public final class CreativeTabIntegration {

    private CreativeTabIntegration() {
    }

    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.SPOTLIGHT.get());
            event.accept(ModBlocks.LIGHT_DIODE.get());
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.OPTICAL_KEY.get());
            event.accept(ModItems.GLOWSTICK.get());
        }
    }
}
