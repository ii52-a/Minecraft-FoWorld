package com.user;

import ii52.FoWorld.FoWorld;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SimpleItemDatagen extends ItemModelProvider {
    public SimpleItemDatagen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, "foworld", existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // 自动循环：给 List 里的每个名字生成一个 JSON
        for (String name : Items.RUNES) {
            withExistingParent(name, "item/generated")
                    .texture("layer0", new ResourceLocation("foworld", "item/" + name));
        }
    }
}