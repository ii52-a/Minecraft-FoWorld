package ii52.FoWorld.blockentity.FlowerEntity;

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
public class LuminexFlowerEntity extends BlockEntity {
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
    public LuminexFlowerEntity(BlockPos pos, BlockState state) {
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
     * 【外部接口】：给右键粉尘逻辑使用的，增加能量的方法。
     */
    public void addGlow(int amount) {
        this.glow = Math.min(this.glow + amount, MAX_GLOW);
        this.setChanged(); // 告诉服务器：数据变了，记得存盘

        // 关键的一步：如果是服务器修改了能量，立即通知周围玩家，这样他们才能看到粒子变化
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public int getGlow() {
        return this.glow;
    }

    public int getMaxGlow() {
        return MAX_GLOW;
    }

    // --- 【心脏跳动】：模组逻辑的动力源 ---

    /**
     * tick 是每秒运行 20 次的函数。这里是所有魔法逻辑的发生地。
     */
    public static void tick(Level level, BlockPos pos, BlockState state, LuminexFlowerEntity be) {
        
        if (!level.isClientSide && level.getGameTime() % 100 == 0 && be.glow > 20) {
            be.glow--;
            be.setChanged(); // 改变了 glow，标记为需要存盘
        }

        // 2. 【核心魔法逻辑】：检测并转化周围的方块
        if (be.glow >= 20) { // 至少要有 20 点能量，这朵花才会“睁开眼睛”看周围

            // 【雷达扫描】：寻找花朵周围 3x3 范围内的普通石头
            BlockPos targetPos = findTargetBlock(level, pos, be.lock);
            if (targetPos != null) {
                // 如果找到了石头，就开始念咒语（进度条增加）
                if (!targetPos.equals(be.lock)) {
                    be.lock = targetPos;
                    be.progress = 0;
                    }
                if (level.getBlockState(targetPos).is(ModTags.Blocks.LUMINEX_FLOWER_STONE)) {
                    stone_to_luminex_stone(level, targetPos, be, pos, state);
                } else if (level.getBlockState(targetPos).is(BlockRegistry.DEEP_GLOW_STONE.get())) {
                    deep_glow_to_glow(level, targetPos, be, pos, state);

                } else {
                    // 如果周围没有石头了，花朵就休息，进度清零
                    be.progress = 0;
                }
            }
        }

        // 3. 【氛围装饰】：只要肚子里有货（能量 > 0），花朵中心就会冒出白色烟雾粒子
        if (level.isClientSide && be.getGlow() > 20 && level.random.nextFloat() < 0.05F + ((float) be.getGlow() / 2000)) {
            level.addParticle(ParticleTypes.END_ROD,
                    pos.getX() + 0.5 + (level.random.nextFloat() - 0.5) * 0.3,
                    pos.getY() + 0.3 + level.random.nextFloat() * 0.5,
                    pos.getZ() + 0.5 + (level.random.nextFloat() - 0.5) * 0.3,
                    0, 0.02, 0);
        }
    }

    /**
     * 【雷达助手】：在花朵 3x3 的地基范围内寻找 Blocks.STONE（普通石头）。
     */
    private static BlockPos findTargetBlock(Level level, BlockPos flowerPos, BlockPos check_pos) {
        // BlockPos.betweenClosed 会返回一个坐标范围内的所有点（就像划拉一个矩形区域）
        // offset(-1, 0, -1) 是左前角，offset(1, 0, 1) 是右后角
        if (check_pos != null && handle_if_transform(level, check_pos)) {
            return check_pos.immutable();
        }
        for (BlockPos p : BlockPos.betweenClosed(flowerPos.offset(-1, 0, -1), flowerPos.offset(1, 0, 1))) {
            // 如果这个位置刚好是石头
            if (handle_if_transform(level, p)) {
                return p.immutable(); // 必须点一下 immutable()，否则这个坐标会在循环里乱跳
            }
        }
        return null; // 啥也没找着
    }

    private static boolean handle_if_transform(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(BlockRegistry.DEEP_GLOW_STONE.get()) || level.getBlockState(pos).is(ModTags.Blocks.LUMINEX_FLOWER_STONE);
    }

    private static void stone_to_luminex_stone(Level level, BlockPos targetPos, LuminexFlowerEntity be, BlockPos pos, BlockState state) {
        be.progress++;
        if (be.progress == be.getMaxGlow() / 3) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(targetPos, BlockRegistry.FLUORITE_ORE_0.get().defaultBlockState());

                // 2. 消耗代价：成功转化一块矿石需要 5 点能量
                be.glow -= 2;
                be.setChanged(); // 标记存盘

                // 3. 全服通报：更新该区域，让所有玩家看到石头变了
                level.sendBlockUpdated(pos, state, state, 3);
            }
        } else if (be.progress == be.getMaxGlow() * 2 / 3) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(targetPos, BlockRegistry.FLUORITE_ORE_1.get().defaultBlockState());

                // 2. 消耗代价：成功转化一块矿石需要 5 点能量
                be.glow -= 2;
                be.setChanged(); // 标记存盘

                // 3. 全服通报：更新该区域，让所有玩家看到石头变了
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }

        // 【炼金成功】：当咒语念满 100 次（5秒）
        if (be.progress >= NEEDED_PROGRESS) {
            if (!level.isClientSide) {
                level.levelEvent(2001, targetPos, net.minecraft.world.level.block.Block.getId(Blocks.STONE.defaultBlockState()));
                level.setBlockAndUpdate(targetPos, BlockRegistry.FLUORITE_ORE.get().defaultBlockState());

                // 2. 消耗代价：成功转化一块矿石需要 5 点能量
                be.glow -= 1;
                be.progress = 0; // 重置计时器，准备转化下一块
                be.setChanged(); // 标记存盘

                // 3. 全服通报：更新该区域，让所有玩家看到石头变了
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

    private static void deep_glow_to_glow(Level level, BlockPos targetPos, LuminexFlowerEntity be, BlockPos pos, BlockState state) {
        be.progress++;
        if (!level.isClientSide) {

            // 【炼金成功】：当咒语念满 100 次（5秒）
            if (be.progress >= NEEDED_PROGRESS) {

                level.levelEvent(2001, targetPos, net.minecraft.world.level.block.Block.getId(Blocks.GLOWSTONE.defaultBlockState()));
                level.setBlockAndUpdate(targetPos, Blocks.GLOWSTONE.defaultBlockState());

                // 2. 消耗代价：成功转化一块需要 4 点能量
                be.glow -= 4;
                be.progress = 0; // 重置计时器，准备转化下一块
                be.setChanged(); // 标记存盘

                // 3. 全服通报：更新该区域，让所有玩家看到石头变了
                level.sendBlockUpdated(pos, state, state, 3);
            }
        }
    }

}


