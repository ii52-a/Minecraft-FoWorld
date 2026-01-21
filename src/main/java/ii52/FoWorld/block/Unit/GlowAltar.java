package ii52.FoWorld.block.Unit;

import ii52.FoWorld.blockentity.UnitEntity.GlowAltarEntity;

import ii52.FoWorld.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import org.jetbrains.annotations.Nullable;


public class GlowAltar extends BaseEntityBlock {

    public GlowAltar(Properties props) {

        super(props);
    }

    /**
     * 【右键交互】
     * 当玩家对着这朵花点右键时触发。
     */
    @Override

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 调试：如果右键了，发送一条只有你能看到的消息（测试完删掉）
        // if (level.isClientSide) player.sendSystemMessage(Component.literal("你点击了祭坛"));

        // 1. 只有主手拿着粉尘才触发
        if (hand == InteractionHand.MAIN_HAND && itemstack.is(ItemRegistry.LUMINESCENT_DUST.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof GlowAltarEntity glowerAltarEntity) {
                    // 这里的 change_base 已经修复了内部的石头判定逻辑
                    boolean succ = glowerAltarEntity.change_base(level);

                    if (succ) {
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        // 成功后播放一个声音，证明逻辑跑通了
                        level.playSound(null, pos, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS; // 如果不是粉尘，让系统处理其他交互
    }

    /**
     * 【渲染形状】
     * 告诉游戏这个方块应该怎么画出来。MODEL 表示按照我们 JSON 文件里定义的模型渲染。
     */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlowAltarEntity(pos, state);
    }

}