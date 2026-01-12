package ii52.FoWorld.registry.BlockRegistry;

import ii52.FoWorld.blockentity.FoBenchBlockEntity;
import ii52.FoWorld.blockentity.LowerLeverCrusherEntity;
import ii52.FoWorld.blockentity.UnitEntity.GlowAltarEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

/**
 * 这个类负责向 Minecraft 注册我们自定义的“方块实体类型”。
 */
public class BlockEntityRegistry {
    // 创建一个延时注册器（DeferredRegister），它就像是一个排队名单。
    // 告诉 Forge：我们要注册的是“方块实体类型”（BLOCK_ENTITY_TYPES），所属模组是 "foworld"。
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "foworld");

    /**
     * 这里是核心：定义并注册“世界工作台”的实体类型。
     * 1. "fo_bench_be" 是这个实体类型在数据库里的名字。
     * 2. () -> ... 是一个工厂：它告诉游戏怎么创建这个实体。
     * 3. Builder.of(...)：
     * - 第一个参数 FoBenchBlockEntity::new 指向它的构造函数。
     * - 第二个参数 BlockRegistry.FO_BENCH.get() 规定这个实体“只准”出现在哪个方块身上。
     */
    public static final RegistryObject<BlockEntityType<FoBenchBlockEntity>> FO_BENCH_BE =
            BLOCK_ENTITIES.register("fo_bench_be", () ->
                    BlockEntityType.Builder.of(FoBenchBlockEntity::new, BlockRegistry.FO_BENCH.get())
                            .build(null) // build(null) 表示目前没有复杂的数据迁移逻辑（DataFixer）
            );
    public static final RegistryObject<BlockEntityType<LowerLeverCrusherEntity>> LOWER_LEVER_CRUSHER =
            BLOCK_ENTITIES.register("lower_lever_crusher", () ->
                    BlockEntityType.Builder.of(LowerLeverCrusherEntity::new, BlockRegistry.LOWER_LEVER_CRUSHER.get())
                            .build(null) // build(null) 表示目前没有复杂的数据迁移逻辑（DataFixer）
            );

    public static final RegistryObject<BlockEntityType<GlowAltarEntity>>GLOW_ALTAR=
            BLOCK_ENTITIES.register("glow_altar", () ->
                    BlockEntityType.Builder.of(GlowAltarEntity::new, BlockRegistry.GLOW_ALTAR.get())
                            .build(null)
            );

    /**
     * 这个方法会在你的主类（FoWorld.java）里被调用。
     * 它的作用是把上面的排队名单（BLOCK_ENTITIES）正式提交给 Forge 的事件总线。
     */
    public static void register(IEventBus bus){
        BLOCK_ENTITIES.register(bus);
    }
}