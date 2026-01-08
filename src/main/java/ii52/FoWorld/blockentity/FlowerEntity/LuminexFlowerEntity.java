package ii52.FoWorld.blockentity.FlowerEntity;

import ii52.FoWorld.registry.FlowerRegistry.FlowerEntityRegistry;
import ii52.FoWorld.registry.FlowerRegistry.FlowerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 这是一个 BlockEntity（实体块），你可以把它理解为方块的“大脑”。
 * 普通方块（Block）不具备记忆，而 BlockEntity 可以存储数据（如：荧光值）。
 */
public class LuminexFlowerEntity extends BlockEntity {

    // 【变量定义】这是花朵存储的私有数据：当前荧光能量值
    private int glow = 0;

    // 【常量定义】荧光值的上限，防止数值溢出或过大
    private static final int MAX_GLOW = 100;

    /**
     * 构造函数：当方块被放置在世界上时，游戏会调用这个方法来创建“大脑”。
     * @param pos 方块在世界里的三维坐标 (x, y, z)
     * @param state 方块的当前状态（比如朝向、是否带水分等）
     */
    public LuminexFlowerEntity(BlockPos pos, BlockState state) {
        // super 必须指向我们在注册表里定义的“大脑类型”(BlockEntityType)
        super(FlowerEntityRegistry.LUMINEX_FLOWER_BE.get(), pos, state);
    }

    // --- 【数据持久化段落】 ---
    // 这里的代码保证了你退出游戏再关掉服务器，花里的能量不会归零。

    /**
     * 【写数据】当游戏保存地图（存档）时，将变量写入 NBT 标签（类似 JSON）。
     */
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        // 将内存里的 glow 变量存入 NBT，起名 "GlowLevel"
        nbt.putInt("GlowLevel", this.glow);
        super.saveAdditional(nbt);
    }

    /**
     * 【读数据】当方块被加载到玩家视野内时，从存档文件读取数据。
     */
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        // 从 NBT 标签里读取名为 "GlowLevel" 的数字，赋值回变量
        this.glow = nbt.getInt("GlowLevel");
    }

    // --- 【业务逻辑段落】 ---

    /**
     * 【修改器】供其他类（比如 Block 类）调用，增加能量。
     */
    public void addGlow(int amount) {
        // 增加能量，但不能超过最大值
        this.glow = Math.min(this.glow + amount, MAX_GLOW);
        System.out.println(this.glow);

        // 【核心操作】告诉游戏：我的数据变了，请尽快帮我存档（存入磁盘）！
        // 如果不写这一行，能量增加后遇到崩溃或强行关机，数据会回滚。
        this.setChanged();
    }

    /**
     * 【访问器】获取当前能量。
     */
    public int getGlow() {
        return this.glow;
    }

    /**
     * 【心脏跳动】这是核心方法，每秒钟会被执行 20 次（20 Ticks = 1 Second）。
     * 注意：它是静态方法(static)，由 Block 类调用来驱动这个“大脑”。
     */
    public static void tick(Level level, BlockPos pos, BlockState state, LuminexFlowerEntity be) {
        // 逻辑前提：只有当花里有能量时，才表现出魔法效果
        if (be.glow > 0) {

            /* // 如果你想让能量随时间慢慢消耗，可以取消下面注释：
            if (level.getGameTime() % 20 == 0) { // 每 20 刻（即每秒）执行一次
                be.glow--;
                be.setChanged();
            }
            */

            // 【视觉效果】
            // 只有客户端（玩家的电脑）才渲染粒子，服务器不处理图形。
            // level.random.nextFloat() < 0.1F 表示大约 10% 的概率产生粒子。
            if (level.isClientSide && level.random.nextFloat() < 0.1F) {
                // 在方块中心周围生成“末地烛”那种白色的闪光粒子
                level.addParticle(ParticleTypes.END_ROD,
                        pos.getX() + 0.5 + (level.random.nextFloat() - 0.5) * 0.3, // X轴随机偏移
                        pos.getY() + 0.3 + level.random.nextFloat() * 0.5,         // Y轴向上偏移
                        pos.getZ() + 0.5 + (level.random.nextFloat() - 0.5) * 0.3, // Z轴随机偏移
                        0, 0.05, 0); // 粒子向上飘动的速度
            }
        }
    }
}