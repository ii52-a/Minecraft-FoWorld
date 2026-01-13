package ii52.FoWorld.event;

import ii52.FoWorld.item.WitheredGlimmerBlade;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "foworld")
public class OnLivingHurt {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 1. event.getSource().getEntity() 返回的是普通的 Entity
        // 我们需要在这里判断它是不是 LivingEntity（因为箭、闪电等也是 Entity，但它们没有主手）
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {

            // 2. 获取武器
            ItemStack weaponStack = attacker.getMainHandItem();

            // 3. 关键：这里判断的是【物品类型】，这不会报错，因为 weaponStack.getItem() 返回的是 Item
            if (weaponStack.getItem() instanceof WitheredGlimmerBlade blade) {

                // 4. 判断【被攻击的目标】
                // event.getEntity() 默认返回的就是 LivingEntity（因为这是 LivingHurtEvent）
                // 所以这里直接用，不需要再 instanceof 了
                LivingEntity target = event.getEntity();

                // 5. 执行你的接口方法
                blade.onWeaponAttack(attacker, target, weaponStack);
            }
        }
    }
}

