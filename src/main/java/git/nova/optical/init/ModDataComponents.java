package git.nova.optical.init;

import com.mojang.serialization.Codec;
import git.nova.optical.OpticalMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(OpticalMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> GLOWSTICK_LIT_AT =
            DATA_COMPONENTS.registerComponentType("glowstick_lit_at", builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG));

    private ModDataComponents() {
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
