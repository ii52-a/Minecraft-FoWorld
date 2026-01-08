package ii52.FoWorld.registry.FlowerRegistry;


import ii52.FoWorld.block.FoBenchBlock;
import ii52.FoWorld.block.flower.LuminexFlower;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FlowerRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "foworld");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "foworld");

    /**
     * 【重点修正位置】
     * 之前你写的是 new Block(...) -> 这会导致游戏忽略你写的 FoBenchBlock 类。
     * 现在改为 new FoBenchBlock(...) -> 这样游戏才会去执行你写的 use() 和 newBlockEntity() 方法。
     */
    // 荧蜕花的方块注册
    public static final RegistryObject<Block> LUMINEX_FLOWER = BLOCKS.register(
            "luminex_flower", // 建议统一用下划线小写，作为 ID
            () -> new LuminexFlower(BlockBehaviour.Properties.copy(Blocks.POPPY) // 建议拷贝 poppy(罂粟) 的属性，自带半透明和无碰撞
                    .instabreak() // 一挖就掉
                    .noOcclusion() // 必须，否则花背后的方块会消失
            )
    );


    /**
     * 每一个方块通常都需要一个对应的物品（BlockItem），这样玩家才能在背包里拿着它。
     */
    public static final RegistryObject<Item> LUMINEX_FLOWER_ITEM = ITEMS.register(
            "luminex_flower", // 这里的 ID 建议和 Block 保持一致
            () -> new BlockItem(LUMINEX_FLOWER.get(), new Item.Properties())
    );

    /**
     * 这个方法由主类（FoWorld.java）调用，负责把上面的“名单”提交给 Forge。
     */
    public static void register(IEventBus bus){
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }
}