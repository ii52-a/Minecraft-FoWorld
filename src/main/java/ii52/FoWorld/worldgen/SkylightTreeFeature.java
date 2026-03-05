package ii52.FoWorld.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
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
        int height = random.nextInt(5) + 10;
        
        for (int y = 0; y < height; y++) {
            BlockState logState = getLogState(y, height, random);
            level.setBlock(pos.above(y), logState, 3);
        }
        
        int leafStart = height - 3;
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = leafStart; y <= height; y++) {
                    if (Math.abs(x) < 2 || Math.abs(z) < 2) {
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
        if (y < 1 || y >= totalHeight - 1) {
            return Blocks.OAK_LOG.defaultBlockState();
        }
        
        float roll = random.nextFloat();
        
        if (roll < 0.40f) {
            return Blocks.OAK_LOG.defaultBlockState();
        } else if (roll < 0.60f) {
            return Blocks.ACACIA_LOG.defaultBlockState();
        }
        
        return Blocks.DARK_OAK_LOG.defaultBlockState();
    }
}
