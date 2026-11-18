package git.nova.optical.init.blocks;

import git.nova.optical.OpticalMod;
import git.nova.optical.common.blockentity.LightDiodeBlockEntity;
import git.nova.optical.common.blockentity.SpotlightBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OpticalMod.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpotlightBlockEntity>> SPOTLIGHT =
            BLOCK_ENTITIES.register("spotlight", () -> BlockEntityType.Builder
                    .of(SpotlightBlockEntity::new, ModBlocks.SPOTLIGHT.get())
                    .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightDiodeBlockEntity>> LIGHT_DIODE =
            BLOCK_ENTITIES.register("light_diode", () -> BlockEntityType.Builder
                    .of(LightDiodeBlockEntity::new, ModBlocks.LIGHT_DIODE.get())
                    .build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
