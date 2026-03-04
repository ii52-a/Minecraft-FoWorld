package ii52.FoWorld.blockentity;

import ii52.FoWorld.registry.EntityRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

/**
 * 这个类就像是“发光小炸弹”的【大脑】。
 * 它决定了炸弹什么时候跳、什么时候炸、炸几次。
 */
public class GlowTntEntity extends PrimedTnt {

    // 记录是谁把这个调皮鬼点燃的（为了知道该把账算在谁头上）
    @Nullable
    private LivingEntity tntOwner;

    // --- 状态小开关 ---
    // 这个开关记录：炸弹是不是已经炸过第一次了？（默认是：还没炸）
    private boolean hasMainExploded = false;
    // 这个是一个小闹钟：第一次炸完后，要等多久才炸第二次？（默认等40下，大约2秒）
    private int postExplosionTimer = 60;

    /**
     * 【方法：找妈妈】
     * 游戏系统用来认出“哦！你是一个发光小炸弹”的固定公式。
     */
    public GlowTntEntity(EntityType<? extends PrimedTnt> type, Level level) {
        super(type, level);
    }

    /**
     * 【方法：出生仪式】
     * 当你用打火石点燃它时，这个方法就会跑起来。
     * 它负责：把炸弹放在地上、让它随机往旁边跳一下、定好4秒后爆炸。
     */
    public GlowTntEntity(Level level, double x, double y, double z, @Nullable LivingEntity igniter) {
        this(EntityRegistry.GLOW_TNT.get(), level);
        this.setPos(x, y, z); // 把炸弹放到你点的那个位置


        double d0 = level.random.nextDouble() * (double)((float)Math.PI * 2F);
        this.setDeltaMovement(-Math.sin(d0) * 0.02D, (double)0.2F, -Math.cos(d0) * 0.02D);

        this.setFuse(80); // 设定保险丝长度：80（也就是4秒钟炸）
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.tntOwner = igniter; // 记住是谁点的火
    }

    /**
     * 【方法：每时每刻都在想什么】
     * 游戏每秒钟会问这个炸弹20次：“你现在在干嘛？”
     * 它是炸弹最核心的逻辑。
     */
    @Override
    public void tick() {
        // --- 第一步：物理模拟（别让自己浮在空中） ---
        // 如果脚下没踩着东西，就给自己一个向下的力（模拟地球引力）
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
        }

        // 按照现在的速度移动位置
        this.move(MoverType.SELF, this.getDeltaMovement());


        this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
        }

        if (!hasMainExploded) {


            int currentFuse = this.getFuse() - 1; // 保险丝烧掉一截
            this.setFuse(currentFuse);


            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.GLOW, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0, 0.1, 0);
            }

            // 如果保险丝烧完了，赶紧触发“第一次大变身”
            if (currentFuse <= 0) {
                if (!this.level().isClientSide) {
                    this.explode();
                }
            }
        } else {

            if (this.level().isClientSide) {
                for (int i = 0; i < 5; i++) {
                    this.level().addParticle(ParticleTypes.ELECTRIC_SPARK, this.getX() + (this.random.nextDouble() - 0.5) * 2, this.getY() + (this.random.nextDouble()) * 2, this.getZ() + (this.random.nextDouble() - 0.5) * 2, 0, 0.2, 0);
                }
            } else {
                // 后台小闹钟滴答滴答走
                postExplosionTimer--;
                // 2秒钟到了！
                if (postExplosionTimer <= 0) {
                    triggerSecondaryExplosions(); // 砰！砰！砰！砰！砰！连炸五下
                    this.discard(); // 累坏了，小炸弹消失，回家睡觉了
                }
            }
        }
    }

    /**
     * 【方法：第一次大变身（主爆炸）】
     * 这一招威力巨大，会把周围的东西都炸飞。
     */
    @Override
    protected void explode() {

        this.level().explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 16.0F, Level.ExplosionInteraction.TNT);

        // 关键：把那个“是否炸过”的开关打开！
        this.hasMainExploded = true;


    }

    /**
     * 【方法：撒欢连环炸】
     * 在刚才炸过的地方周围，随机挑5个幸运位置，再炸一波。
     */
    private void triggerSecondaryExplosions() {
        if (this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 10; i++) {
                // 随机选一个位置（左右10格内，往上3格内）
                double offsetX = (this.random.nextDouble() - 0.5D) * 20.0D;
                double offsetY = (this.random.nextDouble() -0.6D) * 10.0D;
                double offsetZ = (this.random.nextDouble() - 0.5D) * 20.0D;

                double tx = this.getX() + offsetX;
                double ty = this.getY() + offsetY;
                double tz = this.getZ() + offsetZ;

                // 变出好多亮晶晶的粉末和爆炸烟雾
                serverLevel.sendParticles(ParticleTypes.END_ROD, tx, ty, tz, 20, 0.2, 0.2, 0.2, 0.1);


                // 制造一个普通威力的爆炸
                this.level().explode(null, tx, ty, tz, 10.0F, Level.ExplosionInteraction.TNT);
            }
        }
    }

    /**
     * 【方法：身份证】
     * 告诉游戏：“我是发光炸弹，不是普通的红炸弹。”
     */
    @Override
    public EntityType<?> getType() {
        return EntityRegistry.GLOW_TNT.get();
    }

    /**
     * 【方法：谁点的火】
     * 谁点的火，谁就要对这场爆炸负责。
     */
    @Nullable
    @Override
    public LivingEntity getOwner() {
        return this.tntOwner;
    }
}