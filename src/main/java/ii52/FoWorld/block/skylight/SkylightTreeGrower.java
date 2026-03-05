package ii52.FoWorld.block.skylight;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class SkylightTreeGrower extends AbstractTreeGrower {
    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return null;
    }

    @Override
    public boolean growTree(ServerLevel level, ChunkGenerator generator, BlockPos pos, BlockState state, RandomSource random) {
        BlockPos saplingPos = findSaplingCorner(level, pos);
        boolean isLarge = checkLargeSapling(level, saplingPos);
        
        if (isLarge) {
            return growLargeTree(level, saplingPos, random);
        } else {
            return growSmallTree(level, pos, random);
        }
    }

    private BlockPos findSaplingCorner(LevelAccessor level, BlockPos pos) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                BlockPos checkPos = pos.offset(-dx, 0, -dz);
                if (isLargeSaplingCorner(level, checkPos)) {
                    return checkPos;
                }
            }
        }
        return pos;
    }

    private boolean isLargeSaplingCorner(LevelAccessor level, BlockPos pos) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                BlockPos checkPos = pos.offset(dx, 0, dz);
                if (!(level.getBlockState(checkPos).getBlock() instanceof SkylightSaplingBlock)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkLargeSapling(LevelAccessor level, BlockPos pos) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                BlockPos checkPos = pos.offset(dx, 0, dz);
                if (!(level.getBlockState(checkPos).getBlock() instanceof SkylightSaplingBlock)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean growSmallTree(ServerLevel level, BlockPos pos, RandomSource random) {
        int height = Mth.nextInt(random, 10, 14);
        int branchStart = Mth.nextInt(random, 4, 6);

        if (!checkSpace(level, pos, height + 3)) {
            return false;
        }

        setDirtAt(level, pos.below());

        Set<BlockPos> trunkPositions = new HashSet<>();
        for (int y = 0; y < height; y++) {
            BlockPos logPos = pos.above(y);
            BlockState logState = getLogState(y, height, random);
            setBlock(level, logPos, logState);
            trunkPositions.add(logPos);
        }

        generateAcaciaBranches(level, pos, height, branchStart, random, trunkPositions);
        
        BlockPos topPos = pos.above(height - 1);
        generateTopLeaves(level, topPos, random, trunkPositions);

        return true;
    }

    private boolean growLargeTree(ServerLevel level, BlockPos pos, RandomSource random) {
        int height = Mth.nextInt(random, 18, 26);
        int branchStart = Mth.nextInt(random, 6, 9);

        if (!checkSpaceLarge(level, pos, height + 5)) {
            return false;
        }

        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                BlockPos saplingPos = pos.offset(dx, 0, dz);
                level.setBlock(saplingPos, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        setDirtAt(level, pos.below());
        setDirtAt(level, pos.east().below());
        setDirtAt(level, pos.south().below());
        setDirtAt(level, pos.east().south().below());

        Set<BlockPos> trunkPositions = new HashSet<>();
        
        for (int y = 0; y < height; y++) {
            for (int dx = 0; dx <= 1; dx++) {
                for (int dz = 0; dz <= 1; dz++) {
                    BlockPos logPos = pos.offset(dx, y, dz);
                    BlockState logState = getLogState(y, height, random);
                    setBlock(level, logPos, logState);
                    trunkPositions.add(logPos);
                }
            }
        }

        generateLargeAcaciaBranches(level, pos, height, branchStart, random, trunkPositions);
        
        BlockPos topPos = pos.above(height - 1);
        generateLargeTopLeaves(level, topPos, random, trunkPositions);

        return true;
    }

    private void generateTopLeaves(LevelAccessor level, BlockPos topPos, RandomSource random, Set<BlockPos> trunkPositions) {
        int radius = 2 + random.nextInt(2);
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= radius + 0.5) {
                        if (random.nextFloat() < 0.85f || dist < radius - 0.5) {
                            BlockPos leafPos = topPos.offset(x, y, z);
                            if (!trunkPositions.contains(leafPos)) {
                                BlockState leafState = level.getBlockState(leafPos);
                                if (leafState.isAir() || leafState.canBeReplaced()) {
                                    setBlock(level, leafPos, Blocks.OAK_LEAVES.defaultBlockState()
                                            .setValue(LeavesBlock.PERSISTENT, true)
                                            .setValue(LeavesBlock.DISTANCE, 7));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateLargeTopLeaves(LevelAccessor level, BlockPos topPos, RandomSource random, Set<BlockPos> trunkPositions) {
        int radius = 3 + random.nextInt(2);
        
        for (int x = -radius; x <= radius + 1; x++) {
            for (int y = 0; y <= 3; y++) {
                for (int z = -radius; z <= radius + 1; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= radius + 1) {
                        if (random.nextFloat() < 0.8f || dist < radius - 0.5) {
                            BlockPos leafPos = topPos.offset(x, y, z);
                            if (!trunkPositions.contains(leafPos)) {
                                BlockState leafState = level.getBlockState(leafPos);
                                if (leafState.isAir() || leafState.canBeReplaced()) {
                                    setBlock(level, leafPos, Blocks.OAK_LEAVES.defaultBlockState()
                                            .setValue(LeavesBlock.PERSISTENT, true)
                                            .setValue(LeavesBlock.DISTANCE, 7));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateAcaciaBranches(ServerLevel level, BlockPos trunkBase, int height, int branchStart, RandomSource random, Set<BlockPos> trunkPositions) {
        int branchCount = 2 + random.nextInt(3);
        
        for (int i = 0; i < branchCount; i++) {
            int branchHeight = branchStart + random.nextInt(Math.max(1, height - branchStart - 2));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            
            BlockPos branchStartPos = trunkBase.above(branchHeight);
            int branchLength = 2 + random.nextInt(4);
            int upwardSteps = 1 + random.nextInt(3);
            
            BlockPos currentPos = branchStartPos;
            for (int j = 1; j <= branchLength; j++) {
                currentPos = currentPos.relative(direction);
                if (level.getBlockState(currentPos).isAir() || level.getBlockState(currentPos).canBeReplaced()) {
                    BlockState branchState = getLogState(branchHeight, height, random);
                    setBlock(level, currentPos, branchState);
                    trunkPositions.add(currentPos);
                }
            }
            
            for (int u = 0; u < upwardSteps; u++) {
                currentPos = currentPos.above();
                if (level.getBlockState(currentPos).isAir() || level.getBlockState(currentPos).canBeReplaced()) {
                    BlockState branchState = getLogState(branchHeight + u, height, random);
                    setBlock(level, currentPos, branchState);
                    trunkPositions.add(currentPos);
                }
            }
            
            generateLeafCluster(level, currentPos, random, trunkPositions);
        }
    }

    private void generateLargeAcaciaBranches(ServerLevel level, BlockPos trunkBase, int height, int branchStart, RandomSource random, Set<BlockPos> trunkPositions) {
        int branchCount = 4 + random.nextInt(4);
        
        for (int i = 0; i < branchCount; i++) {
            int branchHeight = branchStart + random.nextInt(Math.max(1, height - branchStart - 3));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            
            BlockPos branchStartPos = trunkBase.above(branchHeight);
            if (random.nextBoolean()) {
                branchStartPos = branchStartPos.east();
            }
            if (random.nextBoolean()) {
                branchStartPos = branchStartPos.south();
            }
            
            int branchLength = 3 + random.nextInt(5);
            int upwardSteps = 2 + random.nextInt(4);
            
            BlockPos currentPos = branchStartPos;
            for (int j = 1; j <= branchLength; j++) {
                currentPos = currentPos.relative(direction);
                if (level.getBlockState(currentPos).isAir() || level.getBlockState(currentPos).canBeReplaced()) {
                    BlockState branchState = getLogState(branchHeight, height, random);
                    setBlock(level, currentPos, branchState);
                    trunkPositions.add(currentPos);
                }
            }
            
            for (int u = 0; u < upwardSteps; u++) {
                currentPos = currentPos.above();
                if (level.getBlockState(currentPos).isAir() || level.getBlockState(currentPos).canBeReplaced()) {
                    BlockState branchState = getLogState(branchHeight + u, height, random);
                    setBlock(level, currentPos, branchState);
                    trunkPositions.add(currentPos);
                }
            }
            
            generateLargeLeafCluster(level, currentPos, random, trunkPositions);
        }
    }

    private void generateLeafCluster(LevelAccessor level, BlockPos center, RandomSource random, Set<BlockPos> trunkPositions) {
        int radius = 2 + random.nextInt(2);
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= radius + 0.5) {
                        if (random.nextFloat() < 0.8f || dist < radius - 0.5) {
                            BlockPos leafPos = center.offset(x, y, z);
                            if (!trunkPositions.contains(leafPos)) {
                                BlockState leafState = level.getBlockState(leafPos);
                                if (leafState.isAir() || leafState.canBeReplaced()) {
                                    setBlock(level, leafPos, Blocks.OAK_LEAVES.defaultBlockState()
                                            .setValue(LeavesBlock.PERSISTENT, true)
                                            .setValue(LeavesBlock.DISTANCE, 7));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void generateLargeLeafCluster(LevelAccessor level, BlockPos center, RandomSource random, Set<BlockPos> trunkPositions) {
        int radius = 3 + random.nextInt(2);
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= radius + 0.5) {
                        if (random.nextFloat() < 0.75f || dist < radius - 0.5) {
                            BlockPos leafPos = center.offset(x, y, z);
                            if (!trunkPositions.contains(leafPos)) {
                                BlockState leafState = level.getBlockState(leafPos);
                                if (leafState.isAir() || leafState.canBeReplaced()) {
                                    setBlock(level, leafPos, Blocks.OAK_LEAVES.defaultBlockState()
                                            .setValue(LeavesBlock.PERSISTENT, true)
                                            .setValue(LeavesBlock.DISTANCE, 7));
                                }
                            }
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
            return SkylightRegistry.GLOW_LOG.get().defaultBlockState();
        }
        
        return SkylightRegistry.SKYLIGHT_LOG.get().defaultBlockState();
    }

    private boolean checkSpace(LevelAccessor level, BlockPos pos, int height) {
        for (int y = 0; y <= height; y++) {
            BlockPos checkPos = pos.above(y);
            BlockState checkState = level.getBlockState(checkPos);
            if (!checkState.isAir() && !checkState.canBeReplaced()) {
                if (!(checkState.getBlock() instanceof SkylightSaplingBlock)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkSpaceLarge(LevelAccessor level, BlockPos pos, int height) {
        for (int y = 0; y <= height; y++) {
            for (int dx = -1; dx <= 2; dx++) {
                for (int dz = -1; dz <= 2; dz++) {
                    BlockPos checkPos = pos.offset(dx, y, dz);
                    BlockState checkState = level.getBlockState(checkPos);
                    if (!checkState.isAir() && !checkState.canBeReplaced()) {
                        if (!(checkState.getBlock() instanceof SkylightSaplingBlock)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void setBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 3);
    }

    private void setDirtAt(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() != Blocks.DIRT && state.getBlock() != Blocks.GRASS_BLOCK && state.getBlock() != Blocks.PODZOL) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        }
    }
}
