package ii52.FoWorld.item;

import ii52.FoWorld.registry.EffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GlowingRawBeefItem extends Item {

    public GlowingRawBeefItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            entity.addEffect(new MobEffectInstance(EffectRegistry.GLOW_POISON.get(), 300, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
        }
        return super.finishUsingItem(stack, level, entity);
    }
}
