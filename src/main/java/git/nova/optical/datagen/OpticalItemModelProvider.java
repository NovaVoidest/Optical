package git.nova.optical.datagen;

import git.nova.optical.OpticalMod;
import git.nova.optical.init.items.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class OpticalItemModelProvider extends ItemModelProvider {

    public OpticalItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, OpticalMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("spotlight", modLoc("block/spotlight"));
        withExistingParent("light_diode", modLoc("block/light_diode"));

        basicItem(ModItems.OPTICAL_KEY.get());

        // The glowstick models are hand written (assets/optical/models/item/glowstick*.json)
        // because they use neoforge:separate_transforms to show a flat icon in the GUI and the
        // user-provided 3D model in the hand.
    }
}
