package ii52.FoWorld.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import java.util.UUID;

public class GlowPoisonEffect extends MobEffect {

    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("7107DE5E-7CE8-4030-940E-514C1F160890");

    public GlowPoisonEffect() {
        super(MobEffectCategory.HARMFUL, 0x00FFFF);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();
        if (!level.isClientSide) {
            updateSpeedModifier(entity, amplifier);
            
            entity.setGlowingTag(true);
            
            int lightLevel = level.getMaxLocalRawBrightness(entity.blockPosition());
            int threshold = getLightThreshold(amplifier);
            
            if (lightLevel > threshold) {
                float damageAmount = getDamageAmount(amplifier);
                entity.hurt(level.damageSources().magic(), damageAmount);
            }
        }
    }

    private void updateSpeedModifier(LivingEntity entity, int amplifier) {
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null) return;
        
        speedAttribute.removeModifier(SPEED_MODIFIER_UUID);
        
        double slowAmount = getSlowAmount(amplifier);
        AttributeModifier modifier = new AttributeModifier(
            SPEED_MODIFIER_UUID,
            "glow_poison_slow",
            -slowAmount,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        speedAttribute.addPermanentModifier(modifier);
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, net.minecraft.world.entity.ai.attributes.AttributeMap attributeMap, int amplifier) {
        super.removeAttributeModifiers(entity, attributeMap, amplifier);
        
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null) {
            speedAttribute.removeModifier(SPEED_MODIFIER_UUID);
        }
        
        entity.setGlowingTag(false);
    }

    private double getSlowAmount(int amplifier) {
        return switch (amplifier) {
            case 0 -> 0.05;
            case 1 -> 0.15;
            default -> 0.30;
        };
    }

    private int getLightThreshold(int amplifier) {
        return switch (amplifier) {
            case 0 -> 7;
            case 1 -> 8;
            default -> 9;
        };
    }

    private float getDamageAmount(int amplifier) {
        return amplifier >= 1 ? 1.0F : 0.5F;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int interval = 40;
        return duration % interval == 0;
    }
}
