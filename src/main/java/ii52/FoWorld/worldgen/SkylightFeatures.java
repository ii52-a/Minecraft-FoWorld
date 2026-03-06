package ii52.FoWorld.worldgen;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkylightFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.Keys.FEATURES, "foworld");

    public static final RegistryObject<SkylightTreeFeature> SKYLIGHT_TREE = FEATURES.register(
        "skylight_tree",
        () -> new SkylightTreeFeature()
    );

    public static final RegistryObject<GlowTombFeature> GLOW_TOMB = FEATURES.register(
        "glow_tomb",
        () -> new GlowTombFeature()
    );
}
