package ii52.FoWorld.blockentity.UnitEntity;

import ii52.FoWorld.ModTags;
import ii52.FoWorld.registry.BlockRegistry.BlockRegistry;
import ii52.FoWorld.registry.FlowerRegistry.FlowerEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 欢迎来到 BlockEntity 的世界！
 * 它是方块的“灵魂”或者“内存条”。普通的 Block 只是个空壳，
 * 只有挂载了 BlockEntity，方块才能记住自己的能量值、进度条，甚至能思考周围有什么。
 */
public class GlowAltarEntity extends BlockEntity {
    // 【私有记忆】：这朵花记住了多少能量？
    private int glow = 0;
    // 【私有记忆】：它对着旁边的石头使劲了多久？（进度条）
    private int progress = 0;
    private BlockPos lock = null;

    // 【魔法常数】：能量顶峰是 100
    private static final int MAX_GLOW = 100;
    // 【魔法常数】：转换一块石头需要 100 次“呼吸”（大约 5 秒）
    private static final int NEEDED_PROGRESS = 180;

    /**
     * 构造函数：初始化这朵花的灵魂，绑定它在注册表里的身份。
     */
    public GlowAltarEntity(BlockPos pos, BlockState state) {
        super(FlowerEntityRegistry.LUMINEX_FLOWER_BE.get(), pos, state);
    }

    // --- 【数据持久化】：防止你退出游戏后，“记忆”被重置为零 ---

    /**
     * 【存盘】：把内存里的数据写进硬盘的 NBT 标签里。
     */
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putInt("GlowLevel", this.glow);
        nbt.putInt("TransformProgress", this.progress);
        if (this.lock != null) {
            nbt.putLong("LockPos", this.lock.asLong());
        }
        super.saveAdditional(nbt); // 别忘了叫父类也保存一下它自己的东西
    }

    /**
     * 【读盘】：当世界加载或方块重新进入玩家视野，把数据从硬盘读回内存。
     */
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.glow = nbt.getInt("GlowLevel");
        this.progress = nbt.getInt("TransformProgress");
        if (nbt.contains("LockPos")) {
            this.lock = BlockPos.of(nbt.getLong("LockPos"));
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


    /**
     * tick 是每秒运行 20 次的函数。这里是所有魔法逻辑的发生地。
     */
    public static void tick(Level level, BlockPos pos, BlockState state, GlowAltarEntity be) {

    }






}


