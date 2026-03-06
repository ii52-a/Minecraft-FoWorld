package ii52.FoWorld.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import ii52.FoWorld.block.skylight.SkylightRegistry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SkylightTreeFeature extends Feature<NoneFeatureConfiguration> {
    public static final SkylightTreeFeature INSTANCE = new SkylightTreeFeature();
    
    public SkylightTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        BlockPos pos = context.origin();
        LevelAccessor level = context.level();
        RandomSource random = context.random();
        
        for(int i = 0; i < 10; i++) {
            BlockPos blockpos = pos.offset(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
            if (isValidSpawnPosition(level, blockpos)) {
                growSkylightTree(level, blockpos, random);
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidSpawnPosition(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT);
    }
    
    private void growSkylightTree(LevelAccessor level, BlockPos pos, RandomSource random) {
        int height = random.nextInt(9) + 16;
        
        for (int y = 0; y < height; y++) {
            BlockState logState = getLogState(y, height, random);
            level.setBlock(pos.above(y), logState, 3);
        }
        
        int leafStart = height - 6;
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int y = leafStart; y <= height + 1; y++) {
                    int canopyRadius = Math.max(1, 3 - (y - leafStart) / 2);
                    if (Math.abs(x) <= canopyRadius && Math.abs(z) <= canopyRadius) {
                        BlockPos leafPos = pos.above(y).offset(x, 0, z);
                        if (level.getBlockState(leafPos).isAir()) {
                            level.setBlock(leafPos, Blocks.OAK_LEAVES.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }
    
    private BlockState getLogState(int y, int totalHeight, RandomSource random) {
        if (y < 2 || y >= totalHeight - 2) {
            return SkylightRegistry.SKYLIGHT_LOG.get().defaultBlockState();
        }

        float roll = random.nextFloat();

        if (roll < 0.75f) {
            return SkylightRegistry.SKYLIGHT_LOG.get().defaultBlockState();
        }

        return SkylightRegistry.GLOW_LOG.get().defaultBlockState();
    }
}
