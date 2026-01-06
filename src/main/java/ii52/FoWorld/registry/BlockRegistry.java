package ii52.FoWorld.registry;


import ii52.FoWorld.block.FoBenchBlock;
import ii52.FoWorld.block.LowerLeverCrusherBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "foworld");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "foworld");

    /**
     * 【重点修正位置】
     * 之前你写的是 new Block(...) -> 这会导致游戏忽略你写的 FoBenchBlock 类。
     * 现在改为 new FoBenchBlock(...) -> 这样游戏才会去执行你写的 use() 和 newBlockEntity() 方法。
     */
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

    /**
     * 这个方法由主类（FoWorld.java）调用，负责把上面的“名单”提交给 Forge。
     */
    public static void register(IEventBus bus){
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}