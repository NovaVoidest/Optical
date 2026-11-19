package git.nova.optical.init;

import git.nova.optical.OpticalMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, OpticalMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> SPOTLIGHT_ON = SOUNDS.register("spotlight_on",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(OpticalMod.MODID, "spotlight_on")));

    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }
}
