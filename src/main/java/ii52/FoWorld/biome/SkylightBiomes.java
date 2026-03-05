package ii52.FoWorld.biome;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.registries.ForgeRegistries;

public class SkylightBiomes {
    public static final ResourceKey<Biome> SKYLIGHT_FOREST = ResourceKey.create(ForgeRegistries.Keys.BIOMES, ResourceLocation.fromNamespaceAndPath("foworld", "skylight_forest"));
}
