package git.nova.optical.datagen;

import git.nova.optical.OpticalMod;
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

    }
}
