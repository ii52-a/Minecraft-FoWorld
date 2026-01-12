package ii52.FoWorld.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Test3D extends Block {
    // 定义碰撞箱：参数是 (minX, minY, minZ, maxX, maxY, maxZ)
    // 这里的数值对应模型软件里的 0-16 坐标
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 4.0D, 9.0D, 2.0D, 14.0D);

    public Test3D(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE; // 返回我们定义的扁平碰撞箱
    }


}