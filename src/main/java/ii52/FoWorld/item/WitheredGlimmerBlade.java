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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class WitheredGlimmerBlade extends SwordItem {
    private static final String TAG_ATTRACTS = "attract_count";


    // 构造函数：定义这把镐子的基础属性
    public WitheredGlimmerBlade(Item.Properties properties) {
        super(
                Tiers.STONE,
                4,               // 攻击伤害：在钻石级基础上增加 1 点
                -2.9F,           // 攻击速度：这是攻速偏移量，-2.8 是原版镐子的标准手感
                properties
        );
    }




        // 体现攻击者的动作：
//        attacker.teleportTo(x, y, z); // 让攻击者传送



    private void SetOrUpdateTagAttract(ItemStack stack, Level level) {
        int currentNumber = stack.getOrCreateTag().getInt(TAG_ATTRACTS);
        this.SetOrUpdateTagAttract(stack, level, currentNumber + 1);
    }

    // 方法 B：核心逻辑，接收明确的数字并写入
    private void SetOrUpdateTagAttract(ItemStack stack, Level level, int number) {
        if (!level.isClientSide()) {
            stack.getOrCreateTag().putInt(TAG_ATTRACTS, number);
        }
    }
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repairMaterial) {
        return false; // 铁砧无法放入材料修理
    }

    @Override
    public Component getName(ItemStack stack) {
        // 无论 NBT 里存了什么名字，永远只显示这个固定的名字
        return Component.translatable("item.foworld.withered_glimmer_blade").withStyle(ChatFormatting.AQUA);
    }
    public void onWeaponAttack(LivingEntity attacker, LivingEntity target, ItemStack stack) {
        Level level = attacker.level();

        // 1. 更新 NBT 计数
        this.SetOrUpdateTagAttract(stack, level);

        // 2. 获取当前层数
        int count = stack.getOrCreateTag().getInt(TAG_ATTRACTS);

        // 3. 达到 3 层触发特效
        if (count >= 3) {
            // 逻辑只在服务端运行（生成粒子和添加药水效果必须在服务端）
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {


                attacker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 180, 1)); //
                target.hurt(attacker.damageSources().magic(), 2.0f);
                // --- 效果 B：目标束缚 (短暂减速) ---
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));


                // --- 效果 C：爆发粒子 (圆环或喷涌) ---
                serverLevel.sendParticles(
                        ParticleTypes.END_ROD,
                        target.getX(), target.getY() + 1.0, target.getZ(),
                        10,   // 数量
                        0.5, 0, 0.5, // 范围
                        0.05  // 速度
                );
                // 4. 重置计数为 0
                this.SetOrUpdateTagAttract(stack, level, 0);
                stack.hurtAndBreak(1,attacker,p->{});
            }
        }
    }

    public static RegistryObject<SwordItem> registry(DeferredRegister<Item> items){
        return items.register("withered_glimmer_blade",() ->
                new WitheredGlimmerBlade(new Item.Properties().durability(110))
        );
    }


}
