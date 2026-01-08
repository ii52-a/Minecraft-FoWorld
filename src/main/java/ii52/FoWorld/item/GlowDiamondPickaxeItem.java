package ii52.FoWorld.item;

import ii52.FoWorld.ModTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class GlowDiamondPickaxeItem extends PickaxeItem {
    private static final String TAG_GLOW = "glowing";
    private static final String TAG_NEXT_USE = "NextUseTick";


    // 构造函数：定义这把镐子的基础属性
    public GlowDiamondPickaxeItem(Properties properties) {
        super(
                Tiers.DIAMOND,   // 挖掘等级：设置为钻石级，可以挖黑曜石
                1,               // 攻击伤害：在钻石级基础上增加 1 点
                -2.7F,           // 攻击速度：这是攻速偏移量，-2.8 是原版镐子的标准手感
                properties
        );
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            long now = level.getGameTime();
            long nextUse = stack.getOrCreateTag().getLong(TAG_NEXT_USE);

            if (now < nextUse) {
                return InteractionResultHolder.fail(stack);
            }

            boolean enabled = stack.getOrCreateTag().getBoolean(TAG_GLOW);
            stack.getOrCreateTag().putBoolean(TAG_GLOW, !enabled);

            // 设置 20 tick 冷却
            stack.getOrCreateTag().putLong(TAG_NEXT_USE, now + 10);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    /**
     * 核心逻辑钩子：当玩家用这把镐子【成功破坏】一个方块时，游戏会调用这个方法。
     */
    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {

        boolean enabled= stack.getOrCreateTag().getBoolean(TAG_GLOW);
        if (!enabled)return super.mineBlock(stack, level, state, pos, entity);
        if (level.isClientSide) return true;
        // 2. 身份检查：确保破坏方块的是“玩家”，而不是僵尸或其他生物
        if (!(entity instanceof ServerPlayer player)) return true;

        // 3. 矿雷达检测：调用我们写的检测方法
        // 3 代表半径，即检测以当前方块为中心 7x7x7 的区域
        if (hasNearbyOre(level, pos, 5)) {
            ServerLevel serverLevel = (ServerLevel) level;

            // 4. 视觉反馈：如果附近有矿，生成粒子
            serverLevel.sendParticles(
                    ParticleTypes.END_ROD,    // 粒子类型：白色的末地烛星星
                    pos.getX() + 0.5,         // X：方块中心
                    pos.getY() + 0.7,         // Y：稍微高出一点，防止被地面遮挡
                    pos.getZ() + 0.5,         // Z：方块中心
                    4,                        // 数量：一次喷出 8 个
                    0.4, 0.1, 0.4,            // 扩散系数：水平扩散 0.4，垂直扩散 0.1（形成扁平喷射感）
                    0.05                      // 速度：粒子飘动的初始速度
            );

            // 5. 惩罚机制：因为发动了“感应”，所以额外消耗 1 点耐久
            // 参数 p -> {} 是当物品损坏时的回调，这里留空
            stack.hurtAndBreak(1, player, p -> {});
        }
        stack.hurtAndBreak(1, player, p -> {});

        // 6. 继承父类：最后必须调用 super，确保原版的挖掘逻辑（扣除基础耐久、掉落方块）正常运行
        return super.mineBlock(stack, level, state, pos, entity);
    }

    /**
     * 探测逻辑：遍历周围的方块。
     */
    private boolean hasNearbyOre(Level level, BlockPos center, int radius) {
        // BlockPos.betweenClosed 会返回一个包含该区域内所有坐标的迭代器
        // 它会创建一个立方体，范围从 (center - radius) 到 (center + radius)

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius)
        )) {
            //强制球范围
            if (center.distSqr(pos) > radius * radius) continue;
            // 获取对应坐标的方块状态，并检查它是否是我们想要的矿石
            if (isTargetOre(level.getBlockState(pos))) {
                return true;
            }
        }
        return false;
    }
    @Override
    public Component getName(ItemStack stack) {
        // 无论 NBT 里存了什么名字，永远只显示这个固定的名字
        return Component.translatable("item.foworld.glow_diamond_pickaxe").withStyle(ChatFormatting.AQUA);
    }

    /**
     * 目标判定：定义哪些方块能触发感应。
     */

    private boolean isTargetOre(BlockState state) {
        // 使用 .is() 方法进行比对。建议以后改为使用 Tag（标签）检测，这样可以兼容其他 Mod 的矿石。

        return state.is(ModTags.Blocks.GLOW_CHECK);
    }
    public static RegistryObject<PickaxeItem> registry(DeferredRegister<Item> items){
        return items.register("glow_diamond_pickaxe",() ->
                new GlowDiamondPickaxeItem(new Item.Properties())
        );
    }
}