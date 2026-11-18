package git.nova.optical.init.blocks;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.block.LightDiodeBlock;
import git.nova.optical.common.block.SpotlightBlock;
import git.nova.optical.init.items.ModItems;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.Blocks.createBlocks(OpticalMod.MODID);

    public static final DeferredBlock<SpotlightBlock> SPOTLIGHT = registerBlock("spotlight",
            () -> new SpotlightBlock(BlockBehaviour.Properties.of()
                    .mapColor(DyeColor.LIGHT_GRAY)
                    .strength(3.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .sound(SoundType.COPPER)
                    .lightLevel(state -> state.getValue(SpotlightBlock.LIT) ? 10 : 0)
            ));

    public static final DeferredBlock<LightDiodeBlock> LIGHT_DIODE = registerBlock("light_diode",
            () -> new LightDiodeBlock(BlockBehaviour.Properties.of()
                    .mapColor(DyeColor.YELLOW)
                    .strength(2.0F, 4.0F)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .sound(SoundType.GLASS)
                    .lightLevel(state -> state.getValue(LightDiodeBlock.LIT) ? 15 : 0)
            ));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.registerSimpleBlockItem(name, block, new Item.Properties());
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
