package ii52.FoWorld.block.skylight;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SkylightRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "foworld");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "foworld");

    private static final SkylightTreeGrower SKYLIGHT_TREE_GROWER = new SkylightTreeGrower();

    public static final RegistryObject<Block> SKYLIGHT_LOG = BLOCKS.register(
            "skylight_log",
            () -> new SkylightLogBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG))
    );

    public static final RegistryObject<Block> SKYLIGHT_WOOD = BLOCKS.register(
            "skylight_wood",
            () -> new SkylightWoodBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_WOOD))
    );

    public static final RegistryObject<Block> GLOW_LOG = BLOCKS.register(
            "glow_log",
            () -> new SkylightLogBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG)
                    .lightLevel(state -> 7)
            )
    );

    public static final RegistryObject<Block> GLOW_WOOD = BLOCKS.register(
            "glow_wood",
            () -> new SkylightWoodBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_WOOD)
                    .lightLevel(state -> 7)
            )
    );

    public static final RegistryObject<Block> SKYLIGHT_SAPLING = BLOCKS.register(
            "skylight_sapling",
            () -> new SkylightSaplingBlock(SKYLIGHT_TREE_GROWER, BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING)
                    .instabreak()
                    .noOcclusion()
            )
    );

    public static final RegistryObject<Item> SKYLIGHT_LOG_ITEM = ITEMS.register(
            "skylight_log",
            () -> new BlockItem(SKYLIGHT_LOG.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> SKYLIGHT_WOOD_ITEM = ITEMS.register(
            "skylight_wood",
            () -> new BlockItem(SKYLIGHT_WOOD.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> GLOW_LOG_ITEM = ITEMS.register(
            "glow_log",
            () -> new BlockItem(GLOW_LOG.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> GLOW_WOOD_ITEM = ITEMS.register(
            "glow_wood",
            () -> new BlockItem(GLOW_WOOD.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> SKYLIGHT_SAPLING_ITEM = ITEMS.register(
            "skylight_sapling",
            () -> new BlockItem(SKYLIGHT_SAPLING.get(), new Item.Properties())
    );

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}
