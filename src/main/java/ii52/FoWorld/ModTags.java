package ii52.FoWorld;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> GLOW_CHECK =tag("glow_check");
        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, new ResourceLocation("foworld", name));
        }
    }
    public static class Items {
        // 定义一个名为 non_renameable 的标签
        public static final TagKey<Item> NON_RENAMEABLE = tag("non_renameable");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation("foworld", name));
        }
    }
}