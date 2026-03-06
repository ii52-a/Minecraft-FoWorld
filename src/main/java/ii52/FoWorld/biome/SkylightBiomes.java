package ii52.FoWorld.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class SkylightBiomes {
    public static final ResourceKey<Biome> SKYLIGHT_FOREST = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("foworld", "skylight_forest")
    );
}
