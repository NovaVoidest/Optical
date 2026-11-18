package git.nova.optical.datagen;

import git.nova.optical.OpticalMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class OpticalLanguageProvider extends LanguageProvider {
    public OpticalLanguageProvider(PackOutput output, String locale) {
        super(output, OpticalMod.MODID, locale);
    }

    @Override
    protected void addTranslations() {
    }
}
