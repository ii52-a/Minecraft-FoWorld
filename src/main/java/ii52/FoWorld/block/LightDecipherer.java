package ii52.FoWorld.block;

import ii52.FoWorld.blockentity.FoBenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

// 建议使用 BaseEntityBlock，它比 Block 类多了一些处理实体块的默认工具
public class LightDecipherer extends BaseEntityBlock {

    public LightDecipherer(Properties props) {
        super(props);
    }

    /**
     * 【核心交互】当玩家点击方块时调用。
     * 尽管标记为 Deprecated，但在自定义 Block 类中重写它是定义点击逻辑的唯一方式。
     * Block 类：“这是方块的外壳，管点击。”
     */
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // level.isClientSide 为 true 时是客户端，false 时是服务器端
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);

        }
        // InteractionResult.sidedSuccess 会根据端（客户端/服务器）返回 SUCCESS 或 CONSUME
        // 效果是：玩家手部会摆动一次
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    /**
     * 【渲染模式】告诉游戏如何渲染这个方块。
     * BaseEntityBlock 默认返回 INVISIBLE（不可见），所以必须重写！
     * 返回 MODEL 表示使用我们之前在 JSON 里定义的模型渲染。
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /**
     * 【创建实体】当方块被放置在世界上时，调用此方法产生 BlockEntity。
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FoBenchBlockEntity(pos, state);
    }

    /**
     * 【清理逻辑】当方块被破坏时调用。
     * 如果你的工作台里存了物品，你需要在此时把物品喷出来，否则物品会消失。
     */
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) { // 只有当方块真的被替换（破坏），而不是状态改变时才执行
            BlockEntity be = level.getBlockEntity(pos);

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

}