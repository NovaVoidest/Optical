package git.nova.optical;

import git.nova.optical.client.SpotlightBlockEntityRenderer;
import git.nova.optical.init.blocks.ModBlockEntities;
import git.nova.optical.init.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = OpticalMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OpticalMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class OpticalModClient {
    public OpticalModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerGlowstickProperty(ModItems.GLOWSTICK.get());
            registerGlowstickProperty(ModItems.GLOWSTICK_BLUE.get());
            registerGlowstickProperty(ModItems.GLOWSTICK_PURPLE.get());
            registerGlowstickProperty(ModItems.GLOWSTICK_GREEN.get());
        });
        OpticalMod.LOGGER.info("Optical client setup complete for {}", Minecraft.getInstance().getUser().getName());
    }

    private static void registerGlowstickProperty(net.minecraft.world.item.Item item) {
        ItemProperties.register(
                item,
                ResourceLocation.fromNamespaceAndPath(OpticalMod.MODID, "active"),
                (stack, level, entity, seed) -> git.nova.optical.common.item.GlowstickItem.isLit(stack) ? 1.0F : 0.0F
        );
    }

    @SubscribeEvent
    static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(SpotlightBlockEntityRenderer.HEAD_OFF);
        event.register(SpotlightBlockEntityRenderer.HEAD_ON);
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                ModBlockEntities.SPOTLIGHT.get(),
                SpotlightBlockEntityRenderer::new
        );
    }
}
