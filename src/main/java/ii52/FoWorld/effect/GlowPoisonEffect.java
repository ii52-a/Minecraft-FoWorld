package ii52.FoWorld.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

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
            
            Vec3 lastPos = entity.getPersistentData().contains("LastGlowPos") 
                ? new Vec3(
                    entity.getPersistentData().getDouble("LastGlowPosX"),
                    entity.getPersistentData().getDouble("LastGlowPosY"),
                    entity.getPersistentData().getDouble("LastGlowPosZ")
                ) 
                : null;
            
            Vec3 currentPos = entity.position();
            
            if (lastPos != null && lastPos.distanceToSqr(currentPos) > 0.5) {
                float chance = getChance(amplifier);
                float damageAmount = getDamageAmount(amplifier);
                
                if (entity.getRandom().nextFloat() < chance) {
                    entity.hurt(level.damageSources().magic(), damageAmount);
                }
            }
            
            entity.getPersistentData().putDouble("LastGlowPosX", currentPos.x);
            entity.getPersistentData().putDouble("LastGlowPosY", currentPos.y);
            entity.getPersistentData().putDouble("LastGlowPosZ", currentPos.z);
        }
    }

    private void updateSpeedModifier(LivingEntity entity, int amplifier) {
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute == null) return;
        
        speedAttribute.removeModifier(SPEED_MODIFIER_UUID);
        
        double slowAmount = getSlowAmount(amplifier);
        AttributeModifier modifier = new AttributeModifier(
            SPEED_MODIFIER_UUID,
            "Glow Poison Slow",
            -slowAmount,
            AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        speedAttribute.addPermanentModifier(modifier);
    }

    private double getSlowAmount(int amplifier) {
        return switch (amplifier) {
            case 0 -> 0.05;
            case 1 -> 0.15;
            default -> 0.30;
        };
    }

    private float getChance(int amplifier) {
        return switch (amplifier) {
            case 0 -> 0.10F;
            case 1 -> 0.20F;
            default -> 0.20F;
        };
    }

    private float getDamageAmount(int amplifier) {
        return amplifier >= 2 ? 1.0F : 0.5F;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
