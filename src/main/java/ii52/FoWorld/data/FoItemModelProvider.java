package ii52.FoWorld.data;

import ii52.FoWorld.registry.RuneRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class FoItemModelProvider extends ItemModelProvider {
    public FoItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, "foworld", existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (String path : RuneRegistry.RUNE_PATHS) {
            String name = path.substring(path.lastIndexOf("/") + 1);
            withExistingParent(name, "item/generated")
                    .texture("layer0", new ResourceLocation("foworld", "item/" + path));
        }
    }
}
