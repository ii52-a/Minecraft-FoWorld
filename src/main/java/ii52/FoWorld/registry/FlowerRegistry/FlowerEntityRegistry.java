package ii52.FoWorld.registry.FlowerRegistry;

import ii52.FoWorld.blockentity.FlowerEntity.LuminexFlowerEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FlowerEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "foworld");

    public static final RegistryObject<BlockEntityType<LuminexFlowerEntity>> LUMINEX_FLOWER_BE =
            BLOCK_ENTITIES.register("luminex_flower",
                    () -> BlockEntityType.Builder.of(LuminexFlowerEntity::new,
                            FlowerRegistry.LUMINEX_FLOWER.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}