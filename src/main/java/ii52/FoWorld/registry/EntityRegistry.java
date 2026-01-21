package ii52.FoWorld.registry;

import ii52.FoWorld.FoWorld; // 替换为你的 Mod 主类包名
import ii52.FoWorld.blockentity.GlowTntEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityRegistry {
    // 创建实体类型的注册表
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FoWorld.MODID);

    // 注册点燃后的 TNT 实体
    public static final RegistryObject<EntityType<GlowTntEntity>> GLOW_TNT = ENTITY_TYPES.register("glow_tnt",
            () -> EntityType.Builder.<GlowTntEntity>of(GlowTntEntity::new, MobCategory.MISC)
                    .fireImmune() // 设为免疫火，防止还没炸就被烧掉
                    .sized(0.98F, 0.98F) // TNT 的标准大小
                    .clientTrackingRange(10) // 客户端追踪范围
                    .updateInterval(10) // 更新频率
                    .build("glow_tnt"));

    // 重要：这个方法必须在 Mod 主类的构造函数中调用
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}