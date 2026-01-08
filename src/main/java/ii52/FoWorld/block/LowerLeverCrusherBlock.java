package ii52.FoWorld.block;


import ii52.FoWorld.blockentity.LowerLeverCrusherEntity;
import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

// 建议使用 BaseEntityBlock，它比 Block 类多了一些处理实体块的默认工具
public class LowerLeverCrusherBlock extends BaseEntityBlock {

    public LowerLeverCrusherBlock(Properties props) {
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

            // 只有当 BlockEntity 实现了 MenuProvider 接口（提供 UI 标题和容器）时才能打开界面
            if (be instanceof LowerLeverCrusherEntity entity) {
                // 向玩家发送数据包打开 UI 界面
                NetworkHooks.openScreen((ServerPlayer) player, entity, pos);
            }
        }
        // InteractionResult.sidedSuccess 会根据端（客户端/服务器）返回 SUCCESS 或 CONSUME
        // 效果是：玩家手部会摆动一次
        return InteractionResult.sidedSuccess(level.isClientSide);
    }



    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LowerLeverCrusherEntity(pos, state);
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) { // 只有当方块真的被替换（破坏），而不是状态改变时才执行
            BlockEntity be = level.getBlockEntity(pos);
                // 获取方块实体里的物品处理器
                if (be instanceof LowerLeverCrusherEntity crusherBE) {
                    // 不要用 be.getCapability，直接调用 BE 里的方法或直接访问 handler
                    // 建议在 LowerLeverCrusherEntity 里写一个公共方法 getInventory() 返回 itemHandler
                    dropInventoryItems(level, pos, crusherBE.getItemHandler());

                    level.updateNeighbourForOutputSignal(pos, this);
                }
                // 通知系统：这个坐标的比较器（如果有）需要更新红石信号
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // 只有在服务端运行逻辑才有意义
        return level.isClientSide ? null : createTickerHelper(type, BlockEntityRegistry.LOWER_LEVER_CRUSHER.get(),
                LowerLeverCrusherEntity::tick);
    }
    private void dropInventoryItems(Level level, BlockPos pos, IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                // 真正的掉落执行者
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }
}