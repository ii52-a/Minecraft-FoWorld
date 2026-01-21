package ii52.FoWorld.block;

import ii52.FoWorld.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * GlowTombStone 类
 * 继承自 HorizontalDirectionalBlock，这意味着它只支持四个水平方向（北、南、东、西）
 */
public class GlowTombStone extends HorizontalDirectionalBlock {

    /**
     * 定义方块的状态属性：FACING（朝向）
     * 该属性来自于父类，是一个 DirectionProperty，仅包含水平轴的四个方向
     */
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.player.Player player) {
        // 1. 【安全检查】确保只在服务器端执行生成逻辑（防止客户端生成虚假实体导致闪烁/不同步）
        // 且确保 level 能够转换为 ServerLevel 以使用高级生成方法
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {

            // 2. 【循环生成】召唤 5 只守卫骷髅
            for (int i = 0; i < 2; i++) {
                // 实例化骷髅对象
                net.minecraft.world.entity.monster.Skeleton skeleton = EntityType.SKELETON.create(serverLevel);

                if (skeleton != null) {
                    // 3. 【坐标计算】以墓碑方块中心(0.5)为基准，在 X 和 Z 轴随机正负 1 格范围内散开
                    // 这样可以防止 5 只骷髅堆叠在同一个像素点导致卡死或推挤
                    double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 2.0;
                    double y = pos.getY();
                    double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 2.0;

                    // 4. 【位置初始化】设置实体的坐标、随机水平朝向(Yaw)和仰角(Pitch=0)
                    skeleton.moveTo(x, y, z, level.random.nextFloat() * 360F, 0.0F);

                    // 5. 【系统初始化】重要！调用原版生成终结逻辑
                    // 它会处理实体的基础属性、是否为左撇子、难度加成等。
                    // 必须在 setItemSlot 之前调用，否则系统默认发放的弓会顶掉你的自定义剑。
                    skeleton.finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
                    skeleton.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,1999,2));
                    skeleton.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,1999,2));

                    // 6. 【装备覆盖】强行将骷髅主手的武器替换为你模组中的“超级钻石剑”
                    // 因为此时它没拿弓，骷髅的 AI 会自动切换为“近战模式”
                    skeleton.setItemSlot(EquipmentSlot.HEAD,new ItemStack(Items.IRON_HELMET));
                    skeleton.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(ItemRegistry.WITHERED_GLIMMER_BLADE.get()));

                    // (可选) 设置装备掉落率，0.2f 代表 20% 几率掉落这把剑
                    // skeleton.setDropChance(net.minecraft.world.entity.EquipmentSlot.MAINHAND, 0.2f);

                    // 7. 【激活实体】正式将配置好的实体添加到世界中，此时玩家才能看到它
                    serverLevel.addFreshEntity(skeleton);

                    // 8. 【仇恨锁定】强制将当前破坏墓碑的玩家设为第一攻击目标
                    // 这会让骷髅一出生就处于战斗状态，而不是原地发呆
                    skeleton.setTarget(player);
                }
            }
        }
        // 9. 【父类回调】最后执行父类的破坏逻辑（处理掉落物、经验球等）
        super.playerWillDestroy(level, pos, state, player);
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    // --- 碰撞箱 (VoxelShape) 定义说明 ---
    // Block.box(x1, y1, z1, x2, y2, z2) 参数说明：
    // x1, y1, z1: 起始点坐标（0-16 对应一个像素单位）
    // x2, y2, z2: 结束点坐标
    //

    /**
     * 当方块朝向北 (NORTH, 靠近 Z=0) 时的碰撞箱
     */
    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        // PathComputationType 有三种：LAND (陆地), WATER (水), AIR (飞行)
        // 返回 false 表示该方块是障碍物，AI 会尝试绕路而非直线撞墙
        return false;
    }
    private static final VoxelShape NORTH = Shapes.or(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(7.15, 1.15, 0.65, 9.85, 14.85, 1.85),   // 立柱
            Block.box(2.15, 7.15, 0.65, 14.85, 9.85, 1.85)   // 横梁
    );

    private static final VoxelShape SOUTH = Shapes.or(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(6.15, 1.15, 14.15, 8.85, 14.85, 15.35), // 立柱
            Block.box(1.15, 7.15, 14.15, 13.85, 9.85, 15.35) // 横梁
    );

    private static final VoxelShape WEST = Shapes.or(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0.65, 1.15, 6.15, 1.85, 14.85, 8.85),   // 立柱
            Block.box(0.65, 7.15, 1.15, 1.85, 9.85, 13.85)   // 横梁
    );

    private static final VoxelShape EAST = Shapes.or(
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(14.15, 1.15, 7.15, 15.35, 14.85, 9.85), // 立柱
            Block.box(14.15, 7.15, 2.15, 15.35, 9.85, 14.85) // 横梁
    );

    /**
     * 构造函数
     * @param props 方块属性（如硬度、爆炸抗性、声音等）
     */
    public GlowTombStone(Properties props) {
        super(props);
        // 注册默认状态：如果不指定，初始化时默认朝向北
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    /**
     * 获取方块放置时的状态
     * @param ctx 放置上下文，包含玩家位置、方块朝向等信息
     * @return 返回应当被放置在世界中的 BlockState
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // ctx.getHorizontalDirection(): 获取玩家放置方块时所面对的方向
        // 返回一个包含该方向的 BlockState
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection());
    }

    /**
     * 返回方块在当前位置的形状（碰撞检测和轮廓高亮用）
     * @param state 当前方块的状态（可获取 FACING）
     * @param level 当前所在的世界/维度
     * @param pos   方块的具体坐标
     * @param ctx   碰撞上下文
     * @return      对应的 VoxelShape 形状
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext ctx) {
        // 根据 FACING 属性的值，使用 switch 选择对应的静态 VoxelShape
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case WEST  -> WEST;
            case EAST  -> EAST;
            default    -> NORTH; // 默认返回北向，对应 case NORTH
        };
    }

    /**
     * 定义该方块支持哪些属性
     * @param builder 状态定义构建器
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // 必须将 FACING 添加到构建器中，游戏才能识别并保存该属性
        builder.add(FACING);
    }
}