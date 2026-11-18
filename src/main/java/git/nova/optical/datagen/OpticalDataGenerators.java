package git.nova.optical.datagen;

import git.nova.optical.OpticalMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = OpticalMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class OpticalDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new OpticalRecipeProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new LootTableProvider(
                packOutput,
                Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(OpticalBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider
        ));

        OpticalBlockTagProvider blockTagProvider = generator.addProvider(
                event.includeServer(),
                new OpticalBlockTagProvider(packOutput, lookupProvider, existingFileHelper)
        );
        generator.addProvider(
                event.includeServer(),
                new OpticalItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper)
        );

        generator.addProvider(event.includeClient(), new OpticalBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new OpticalItemModelProvider(packOutput, existingFileHelper));
    }
}
