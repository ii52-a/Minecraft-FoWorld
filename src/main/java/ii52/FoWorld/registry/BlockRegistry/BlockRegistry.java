package ii52.FoWorld.registry.BlockRegistry;


import ii52.FoWorld.block.FoBenchBlock;
import ii52.FoWorld.block.GlowTntBlock;
import ii52.FoWorld.block.GlowTombStone;
import ii52.FoWorld.block.LowerLeverCrusherBlock;
import ii52.FoWorld.block.Unit.GlowAltar;
import ii52.FoWorld.block.Unit.LightVeinedStone;
import ii52.FoWorld.blockentity.GlowTntEntity;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "foworld");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "foworld");


    public static final RegistryObject<Block> FO_BENCH = BLOCKS.register(
            "fo_bench",
            () -> new FoBenchBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)
                    .strength(2.0f)
                    // 建议加上 noOcclusion，如果你的模型不是完整的正方体，这能防止透视导致的周围方块不渲染
                    .noOcclusion()
            )
    );
    public static final RegistryObject<Block> LOWER_LEVER_CRUSHER = BLOCKS.register(
            "lower_lever_crusher",
            () -> new LowerLeverCrusherBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)
                    .strength(2.0f)
                    // 建议加上 noOcclusion，如果你的模型不是完整的正方体，这能防止透视导致的周围方块不渲染
                    .noOcclusion()
            )
    );

    public static final RegistryObject<Block> FLUORITE_ORE = BLOCKS.register(
            "fluorite_ore", // 注册名
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                      .strength(1.5f, 3.0f)
                      .lightLevel(state -> 4)
//                    .requiresCorrectToolForDrops() // 设置必须用稿子挖才能掉落
            )
    );
    public static final RegistryObject<Block> FLUORITE_ORE_0 = BLOCKS.register(
            "fluorite_ore_0", // 注册名
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(1.5f, 3.0f)
//                    .requiresCorrectToolForDrops() // 设置必须用稿子挖才能掉落
            )
    );
    public static final RegistryObject<Block> FLUORITE_ORE_1 = BLOCKS.register(
            "fluorite_ore_1", // 注册名
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(1.5f, 3.0f)
//                    .requiresCorrectToolForDrops() // 设置必须用稿子挖才能掉落
            )
    );
    public static final RegistryObject<Block> DEEP_GLOW_STONE = BLOCKS.register(
            "deep_glow_stone", // 注册名
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.GLOWSTONE)
                    .strength(0.3f, 2.0f)
                    .lightLevel(state -> 8)   // 自带微光，很有氛围感
                    .sound(SoundType.GLASS)
//                    .requiresCorrectToolForDrops() // 设置必须用稿子挖才能掉落
            )
    );
    //辉光祭坛
    public static final RegistryObject<Block> GLOW_ALTAR = BLOCKS.register(
            "glow_altar", // 注册名
            () -> new GlowAltar(BlockBehaviour.Properties.copy(Blocks.STONE)
            )
    );
    public static final RegistryObject<Block> LIGHT_VEINED_STONE = BLOCKS.register(
            "light_veined_stone", // 注册名
            () -> new LightVeinedStone(BlockBehaviour.Properties.copy(Blocks.STONE)
            )
    );

    public static final RegistryObject<Block> CHISELED_GLOW_VEIN_QUARTZ = BLOCKS.register(
            "chiseled_glow_vein_quartz", // 注册名
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
            )
    );
    public static final RegistryObject<Block> GLOW_TNT = BLOCKS.register("glow_tnt",
            () -> new GlowTntBlock(BlockBehaviour.Properties.copy(Blocks.TNT))
    );

    //辉光坟墓
    public static final RegistryObject<Block> GLOW_TOMBSTONE = BLOCKS.register("glow_tombstone",
            () -> new GlowTombStone(BlockBehaviour.Properties.copy(Blocks.STONE) // 1. 复制石头属性
                    .strength(2.0f)           // 2. 在属性上设置硬度
                    .requiresCorrectToolForDrops() // 3. 在属性上设置需要工具
                    .sound(SoundType.STONE)    // 4. 在属性上设置声音
                    .noOcclusion()             // 5. 在属性上关闭遮挡剔除
            )
    );

    /**
     * 每一个方块通常都需要一个对应的物品（BlockItem），这样玩家才能在背包里拿着它。
     */
    public static final RegistryObject<Item> FO_BENCH_ITEM = ITEMS.register(
            "fo_bench",
            () -> new BlockItem(FO_BENCH.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> LOWER_LEVER_CRUSHER_ITEM = ITEMS.register(
            "lower_lever_crusher",
            () -> new BlockItem(LOWER_LEVER_CRUSHER.get(), new Item.Properties())
    );
    //荧光石
    public static final RegistryObject<Item> FLUORITE_ORE_ITEM = ITEMS.register(
            "fluorite_ore",
            () -> new BlockItem(FLUORITE_ORE.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> FLUORITE_ORE_0_ITEM = ITEMS.register(
            "fluorite_ore_0",
            () -> new BlockItem(FLUORITE_ORE_0.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> FLUORITE_ORE_1_ITEM = ITEMS.register(
            "fluorite_ore_1",
            () -> new BlockItem(FLUORITE_ORE_1.get(), new Item.Properties())
    );
    //暗光萤石
    public static final RegistryObject<Item> DEEP_GLOW_STONE_ITEM = ITEMS.register(
            "deep_glow_stone",
            () -> new BlockItem(DEEP_GLOW_STONE.get(), new Item.Properties())
    );
    //辉光祭坛
    public static final RegistryObject<Item> GLOW_ALTAR_ITEM = ITEMS.register(
            "glow_altar",
            () -> new BlockItem(GLOW_ALTAR.get(), new Item.Properties())
    );
    //
    public static final RegistryObject<Item> LIGHT_VEINED_STONE_ITEM = ITEMS.register(
            "light_veined_stone",
            () -> new BlockItem(LIGHT_VEINED_STONE.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> GLOWTOMBSTONE_ITEM = ITEMS.register(
            "glow_tombstone",
            () -> new BlockItem(GLOW_TOMBSTONE.get(), new Item.Properties())
    );
    public static final RegistryObject<Item> GLOW_TNT_ITEM = ITEMS.register(
            "glow_tnt",
            () -> new BlockItem(GLOW_TNT.get(), new Item.Properties())
    );

    public static final RegistryObject<Item> CHISELED_GLOW_VEIN_QUARTZ_ITEM = ITEMS.register(
            "chiseled_glow_vein_quartz",
            () -> new BlockItem(CHISELED_GLOW_VEIN_QUARTZ.get(), new Item.Properties())
    );

//chiseled_glow_vein_quartz



    /**
     * 这个方法由主类（FoWorld.java）调用，负责把上面的“名单”提交给 Forge。
     */
    public static void register(IEventBus bus){
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}