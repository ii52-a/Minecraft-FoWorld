package ii52.FoWorld.block.flower;

import ii52.FoWorld.blockentity.FlowerEntity.LuminexFlowerEntity;
import ii52.FoWorld.registry.FlowerRegistry.FlowerEntityRegistry;
import ii52.FoWorld.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * LuminexFlower（荧蜕花）类
 * 继承 FlowerBlock：让它拥有“花”的所有特性（只能种在土上、有碰撞箱等）。
 * 实现 EntityBlock：让它拥有“存储数据”的能力（绑定 BlockEntity）。
 */
public class LuminexFlower extends FlowerBlock implements EntityBlock {

    public LuminexFlower(Properties props) {
        // super 调用父类构造函数：
        // 1. 设置这朵花在某些特定情况下提供的药水效果（发光效果）。
        // 2. 设置效果持续时间。
        super(() -> MobEffects.GLOWING, 100, props);
    }

    /**
     * 【右键交互】
     * 当玩家对着这朵花点右键时触发。
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 获取玩家当前手上拿着的物品堆
        ItemStack itemstack = player.getItemInHand(hand);

        // 逻辑通常只在服务器端运行，防止客户端出现“影子数据”
        if (!level.isClientSide) {
            // 检查玩家手里拿的是不是我们注册的“荧光粉尘”
            if (itemstack.is(ItemRegistry.LUMINESCENT_DUST.get())) {
                // 尝试获取该位置的“大脑”（BlockEntity）
                BlockEntity be = level.getBlockEntity(pos);

                if (be instanceof LuminexFlowerEntity flowerEntity) {
                    if (flowerEntity.getGlow() <flowerEntity.getMaxGlow()*0.9){
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }

                        // 2. 增加能量：调用 BlockEntity 里的方法增加 glow 值
                        flowerEntity.addGlow(10);

                        // 3. 反馈：在方块位置播放一个清脆的声音（紫水晶簇音效）
                        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.0F);

                        return InteractionResult.SUCCESS; // 告诉游戏：交互成功，别再做别的了
                    }
                    // 1. 消耗粉尘：如果不是创造模式，则扣除一个
                    else{
                        return InteractionResult.FAIL;
                    }
                }
            }
        }
        // 在客户端返回 sidedSuccess，会让玩家的手部摆动，看起来更有真实感
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /**
     * 【渲染形状】
     * 告诉游戏这个方块应该怎么画出来。MODEL 表示按照我们 JSON 文件里定义的模型渲染。
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /**
     * 【创建大脑】
     * 每当世界上放置一朵这种花时，系统会自动调用这个方法，产生对应的 LuminexFlowerEntity。
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LuminexFlowerEntity(pos, state);
    }


    /**
     * 【心跳绑定】
     * 这一步非常关键！它把 Block（身体）和 BlockEntity（大脑）的 Tick（逻辑循环）联通起来。
     * 没有这个方法，LuminexFlowerEntity 里的 tick 方法永远不会运行。
     */
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // 检查传入的类型是否是我们注册的那种大脑
         System.out.println("Ticker 请求类型: " + (type ==FlowerEntityRegistry.LUMINEX_FLOWER_BE.get()));
        if (type == FlowerEntityRegistry.LUMINEX_FLOWER_BE.get()) {
            // 返回一个 lambda 表达式：告诉系统每过 1 刻，就去执行我们 Entity 类里的 tick 静态方法
            return (lvl, p, st, be) -> {
                if (be instanceof LuminexFlowerEntity flower) {
                    LuminexFlowerEntity.tick(lvl, p, st, flower);
                }
            };
        }
        return null;
    }
}