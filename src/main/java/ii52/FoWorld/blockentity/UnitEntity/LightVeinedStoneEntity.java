package ii52.FoWorld.blockentity.UnitEntity;

import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LightVeinedStoneEntity extends BlockEntity{

    private BlockPos corePos; // 存储核心的坐标

    public LightVeinedStoneEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LIGHT_VEINED_STONE.get(), pos, state);
    }
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt); // 别忘了叫父类也保存一下它自己的东西
        if (this.corePos != null) {
            // 使用官方工具类将 BlockPos 转换为 CompoundTag
            nbt.put("CorePos", NbtUtils.writeBlockPos(this.corePos));
        }
    }

    /**
     * 【读盘】：当世界加载或方块重新进入玩家视野，把数据从硬盘读回内存。
     */
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("CorePos", 10)) { // 10 代表 CompoundTag 类型
            this.corePos = NbtUtils.readBlockPos(nbt.getCompound("CorePos"));
        }

    }
    public void setCorePos(BlockPos pos) {
        this.corePos = pos;
        this.setChanged();
    }
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt); // 直接复用存盘逻辑，把 glow 和 progress 都塞进去
        return nbt;
    }

    public void notifyCore(Level level,BlockPos WorldPos) {
        if (this.corePos == null || level == null) return;
        if (!level.isLoaded(this.corePos)) return;
        if (corePos != null) {
            BlockEntity be = level.getBlockEntity(corePos);
            if (be instanceof GlowAltarEntity core) {
                core.onPartRemoved(WorldPos); // 通知核心：我没了
            }
        }
    }
}
