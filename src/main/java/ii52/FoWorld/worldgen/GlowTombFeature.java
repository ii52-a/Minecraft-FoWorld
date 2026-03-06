package ii52.FoWorld.worldgen;

import ii52.FoWorld.registry.BlockRegistry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class GlowTombFeature extends Feature<NoneFeatureConfiguration> {
    public GlowTombFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, origin.getX(), origin.getZ());
        BlockPos baseCenter = new BlockPos(origin.getX(), y - 1, origin.getZ());
        BlockPos tombPos = baseCenter.above();

        BlockState ground = level.getBlockState(baseCenter);
        if (!ground.is(Blocks.GRASS_BLOCK) && !ground.is(Blocks.DIRT) && !ground.is(Blocks.COARSE_DIRT)) {
            return false;
        }

        if (!level.getBlockState(tombPos).isAir()) {
            return false;
        }

        BlockState baseState = BlockRegistry.CHISELED_GLOW_VEIN_QUARTZ.get().defaultBlockState();
        BlockState cornerState = BlockRegistry.DEEP_GLOW_STONE.get().defaultBlockState();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos basePos = baseCenter.offset(dx, 0, dz);
                level.setBlock(basePos, baseState, 3);
            }
        }

        level.setBlock(baseCenter.offset(-1, 0, -1), cornerState, 3);
        level.setBlock(baseCenter.offset(-1, 0, 1), cornerState, 3);
        level.setBlock(baseCenter.offset(1, 0, -1), cornerState, 3);
        level.setBlock(baseCenter.offset(1, 0, 1), cornerState, 3);

        level.setBlock(tombPos, BlockRegistry.GLOW_TOMBSTONE.get().defaultBlockState(), 3);

        if (random.nextFloat() < 0.35f) {
            level.setBlock(tombPos.above(), Blocks.SOUL_LANTERN.defaultBlockState(), 3);
        }

        return true;
    }
}
