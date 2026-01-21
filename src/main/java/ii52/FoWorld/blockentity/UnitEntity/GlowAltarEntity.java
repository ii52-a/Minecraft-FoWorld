package ii52.FoWorld.blockentity.UnitEntity;


import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
import ii52.FoWorld.registry.BlockRegistry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 欢迎来到 BlockEntity 的世界！
 * 它是方块的“灵魂”或者“内存条”。普通的 Block 只是个空壳，
 * 只有挂载了 BlockEntity，方块才能记住自己的能量值、进度条，甚至能思考周围有什么。
 */
public class GlowAltarEntity extends BlockEntity {

    private List<BlockPos> LinkListPos=List.of(
            new BlockPos(-2, 0, -2), new BlockPos(-2, 0, 2),
            new BlockPos(2, 0, -2),  new BlockPos(2, 0, 2),
            new BlockPos(0, 0, 3), new BlockPos(0, 0, -3),
            new BlockPos(3, 0, 0),  new BlockPos(-3, 0, 0)
    );;
    private boolean is_active=false;
    private int link_place=0;
    private List<Boolean> link_list =new ArrayList<>(Collections.nCopies(8, false));
    /**
     * 构造函数：初始化这朵花的灵魂，绑定它在注册表里的身份。
     */
    public GlowAltarEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.GLOW_ALTAR.get(), pos, state);
    }
    public boolean getActiveStatus(){
        return this.is_active;
    }

    public boolean change_base(Level level){
        if (!level.isClientSide()){
            int index=lock_base_place();
            if (index!=-1){
                BlockPos target = this.worldPosition.offset(LinkListPos.get(index));
                if (level.getBlockState(target).is(Blocks.STONE)){
                    level.setBlockAndUpdate(target, BlockRegistry.LIGHT_VEINED_STONE.get().defaultBlockState());
                    BlockEntity subBE = level.getBlockEntity(target);
                    if (subBE instanceof LightVeinedStoneEntity child) {
                        child.setCorePos(this.worldPosition);
                    }
                    link_list.set(index, true);
                    this.link_place++;
                    level.levelEvent(2001, target, net.minecraft.world.level.block.Block.getId(Blocks.STONE.defaultBlockState()));
                    check_active();
                    this.setChanged();
                    return true;
                }
                check_active();
                return false;
            }
            check_active();
            return false;
        }
        return false;
    }


    public int lock_base_place(){
        return link_list.indexOf(false);
    }
    public void onPartRemoved(BlockPos pos){
        BlockPos relative = pos.subtract(this.worldPosition);
        int index=this.LinkListPos.indexOf(relative);
        link_list.set(index,false);
        System.out.println(link_place);
        link_place--;
        check_active();
        this.setChanged();
    }
    public void check_active(){
        this.is_active= link_place == 8;
    }
    // --- 【数据持久化】：防止你退出游戏后，“记忆”被重置为零 ---
    /**
     * 【存盘】：把内存里的数据写进硬盘的 NBT 标签里。
     */
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putBoolean("is_active", this.is_active);
        nbt.putInt("link_place", this.link_place);
        byte[] bools = new byte[link_list.size()];
        for (int i = 0; i < link_list.size(); i++) {
            bools[i] = (byte) (link_list.get(i) ? 1 : 0);
        }
        nbt.putByteArray("link_list", bools);
        super.saveAdditional(nbt); // 别忘了叫父类也保存一下它自己的东西
    }

    /**
     * 【读盘】：当世界加载或方块重新进入玩家视野，把数据从硬盘读回内存。
     */
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.is_active = nbt.getBoolean("is_active");
        this.link_place = nbt.getInt("link_place");
        if (nbt.contains("link_list")) {
            byte[] bools = nbt.getByteArray("link_list");
            for (int i = 0; i < bools.length && i < link_list.size(); i++) {
                link_list.set(i, bools[i] == 1);
            }
        }
    }

    // --- 【网络同步】：让服务器上的“真数据”能传给玩家电脑上的“假图像” ---

    /**
     * 当客户端需要同步所有数据时（比如玩家刚走过来），打包 NBT。
     */
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt); // 直接复用存盘逻辑，把 glow 和 progress 都塞进去
        return nbt;
    }

    /**
     * 当数据发生细微变化时，发送一个精准的“电报”给客户端。
     */
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // 1.20.1 的标准发包方式：ClientboundBlockEntityDataPacket
        return ClientboundBlockEntityDataPacket.create(this);
    }







}


