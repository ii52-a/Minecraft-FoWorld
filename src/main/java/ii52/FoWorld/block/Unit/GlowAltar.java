package ii52.FoWorld.block.Unit;

import ii52.FoWorld.blockentity.UnitEntity.GlowAltarEntity;
import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class GlowAltar extends BaseEntityBlock {

    public GlowAltar(Properties props) {

        super(props);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (hand == InteractionHand.MAIN_HAND && itemstack.is(ItemRegistry.LUMINESCENT_DUST.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof GlowAltarEntity glowerAltarEntity) {
                    boolean succ = glowerAltarEntity.change_base(level);

                    if (succ) {
                        if (!player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }
                        level.playSound(null, pos, SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.BLOCKS, 1.0f, 1.0f);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (hand == InteractionHand.MAIN_HAND && !itemstack.isEmpty() && !itemstack.is(ItemRegistry.LUMINESCENT_DUST.get())) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof GlowAltarEntity glowerAltarEntity) {
                    if (glowerAltarEntity.getActiveStatus()) {
                        boolean success = glowerAltarEntity.distributeItemToPedestal(level, itemstack);
                        if (success) {
                            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 1.0f);
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof GlowAltarEntity glowAltarEntity){
                glowAltarEntity.dropAllPedestalItems(level);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GlowAltarEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.GLOW_ALTAR.get(), 
            (lvl, pos, st, be) -> be.tick(lvl));
    }

}
