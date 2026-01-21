package ii52.FoWorld.blockentity;

import ii52.FoWorld.registry.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class GlowTntEntity extends PrimedTnt {
    @Nullable
    private LivingEntity tntOwner;

    // --- 新增控制变量 ---
    private boolean hasMainExploded = false; // 标记主爆炸是否已发生
    private int postExplosionTimer = 40;     // 延迟计时器（40 tick = 2秒）

    public GlowTntEntity(EntityType<? extends PrimedTnt> type, Level level) {
        super(type, level);
    }

    public GlowTntEntity(Level level, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(EntityRegistry.GLOW_TNT.get(), level);
        this.setPos(x, y, z);
        double d0 = level.random.nextDouble() * (double)((float)Math.PI * 2F);
        this.setDeltaMovement(-Math.sin(d0) * 0.02D, (double)0.2F, -Math.cos(d0) * 0.02D);
        this.setFuse(80);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.tntOwner = igniter;
    }

    /**
     * 第一阶段：主爆炸
     * 当倒计时结束，这个方法会被系统调用
     */
    @Override
    protected void explode() {
        // 1. 触发主爆炸 (威力 20.0)
        this.level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 20.0F, Level.ExplosionInteraction.TNT);

        // 2. 核心改动：标记已经主爆，不再销毁实体，进入计时状态
        this.hasMainExploded = true;

        // 3. 视觉冲击：主爆瞬间产生一个巨大的闪光粒子（Flash）
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 2, 0, 0, 0, 0);
        }
    }

    /**
     * 第二阶段：计时器与粒子监听
     * 每 tick 执行一次
     */
    @Override
    public void tick() {
        // 如果主爆炸还没发生，正常走原版的倒计时逻辑
        if (!hasMainExploded) {
            super.tick();

            // 常规等待时的粒子（原有的逻辑）
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.GLOW, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0.1, 0);
            }
        } else {
            // --- 主爆炸已发生，进入 2 秒倒计时逻辑 ---

            // 1. 核心视觉：爆炸残余能量的粒子效果（让爆炸点看起来在“充能”）
            if (this.level().isClientSide) {
                for (int i = 0; i < 5; i++) {
                    // 产生向中心汇聚或向外喷涌的粒子
                    this.level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                            this.getX() + (this.random.nextDouble() - 0.5) * 2,
                            this.getY() + (this.random.nextDouble()) * 2,
                            this.getZ() + (this.random.nextDouble() - 0.5) * 2,
                            0, 0.2, 0);
                }
            }

            // 2. 计时器递减
            if (!this.level().isClientSide) {
                postExplosionTimer--;

                // 3. 第三阶段：2秒时间到，触发二次连环爆炸
                if (postExplosionTimer <= 0) {
                    triggerSecondaryExplosions()
                    this.discard(); // 最终销毁实体
                }
            }
        }
    }

    /**
     * 辅助方法：触发那 5 个分布的小爆炸
     */
    private void triggerSecondaryExplosions() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 5; i++) {
                double offsetX = (this.random.nextDouble() - 0.5D) * 10.0D;
                double offsetY = this.random.nextDouble() * 3.0D;
                double offsetZ = (this.random.nextDouble() - 0.5D) * 10.0D;

                double tx = this.getX() + offsetX;
                double ty = this.getY() + offsetY;
                double tz = this.getZ() + offsetZ;

                // 粒子效果
                serverLevel.sendParticles(ParticleTypes.GLOW, tx, ty, tz, 40, 0.5, 0.5, 0.5, 0.2);
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, tx, ty, tz, 1, 0, 0, 0, 0);

                // 二次爆炸逻辑
                this.level().explode(null, tx, ty, tz, 4.0F, Level.ExplosionInteraction.TNT);
            }
        }
    }

    @Override
    public EntityType<?> getType() {
        return EntityRegistry.GLOW_TNT.get();
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        return this.tntOwner;
    }
}