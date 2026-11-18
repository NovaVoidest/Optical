package git.nova.optical;

import git.nova.optical.init.ModDataComponents;
import git.nova.optical.init.blocks.ModBlockEntities;
import git.nova.optical.init.blocks.ModBlocks;
import git.nova.optical.init.creativetabs.CreativeTabIntegration;
import git.nova.optical.init.items.ModItems;
import git.nova.optical.network.OpticalNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

@Mod(OpticalMod.MODID)
public class OpticalMod {
    public static final String MODID = "optical";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OpticalMod(IEventBus modEventBus, ModContainer modContainer) {
        ModDataComponents.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        modEventBus.addListener(CreativeTabIntegration::onBuildContents);

        modEventBus.addListener(OpticalNetwork::register);
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Optical common setup complete");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
