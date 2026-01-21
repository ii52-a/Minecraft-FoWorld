package ii52.FoWorld.block;

import ii52.FoWorld.blockentity.GlowTntEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class GlowTntBlock extends TntBlock {
    public GlowTntBlock(Properties properties) {
        super(properties);
    }


    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        if (!level.isClientSide) {
            GlowTntEntity tnt = new GlowTntEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, explosion.getIndirectSourceEntity());
            int fuse = tnt.getFuse();
            tnt.setFuse((short)(level.random.nextInt(fuse / 4) + fuse / 8));
            level.addFreshEntity(tnt);
        }
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter) {
        if (!level.isClientSide) {
            // 创建自定义实体
            GlowTntEntity tnt = new GlowTntEntity(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, igniter);
            level.addFreshEntity(tnt);
            // 播放点燃音效
            level.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(),
                    SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            // 移除方块
            level.removeBlock(pos, false);
        }
    }
}