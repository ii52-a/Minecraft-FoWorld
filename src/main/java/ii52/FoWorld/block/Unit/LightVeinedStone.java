package ii52.FoWorld.block.Unit;

import ii52.FoWorld.blockentity.UnitEntity.GlowAltarEntity;
import ii52.FoWorld.blockentity.UnitEntity.LightVeinedStoneEntity;
import ii52.FoWorld.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LightVeinedStone extends Block implements EntityBlock {
    public LightVeinedStone(Properties props) {
        super(props);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.player.Player player) {
        // 1. 【安全检查】确保只在服务器端执行生成逻辑（防止客户端生成虚假实体导致闪烁/不同步）
        // 且确保 level 能够转换为 ServerLevel 以使用高级生成方法
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof LightVeinedStoneEntity lightVeinedStoneEntity){
                lightVeinedStoneEntity.notifyCore(level,pos);
            }
        }
        // 9. 【父类回调】最后执行父类的破坏逻辑（处理掉落物、经验球等）
        super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LightVeinedStoneEntity(pos, state);
    }
}
