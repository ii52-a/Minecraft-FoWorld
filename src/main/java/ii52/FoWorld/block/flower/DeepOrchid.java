package ii52.FoWorld.block.flower;


import net.minecraft.core.BlockPos;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;


/**
 * LuminexFlower（荧蜕花）类
 * 继承 FlowerBlock：让它拥有“花”的所有特性（只能种在土上、有碰撞箱等）。
 * 实现 EntityBlock：让它拥有“存储数据”的能力（绑定 BlockEntity）。
 */
public class DeepOrchid extends FlowerBlock{

    public DeepOrchid(Properties props) {

        super(() -> MobEffects.GLOWING, 100, props);
    }
    @Override
    protected boolean mayPlaceOn(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        return state.is(Blocks.STONE)
                || state.is(Blocks.MOSSY_COBBLESTONE)
                || state.is(Blocks.DEEPSLATE)          // 基础深板岩
                || state.is(Blocks.COBBLED_DEEPSLATE)  // 圆石深板岩
                || super.mayPlaceOn(state, level, pos);
    }
}