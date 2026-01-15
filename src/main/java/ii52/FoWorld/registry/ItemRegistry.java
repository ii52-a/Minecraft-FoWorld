package ii52.FoWorld.registry;

import ii52.FoWorld.item.GlowDiamondPickaxeItem;
import ii52.FoWorld.item.WitheredGlimmerBlade;
import ii52.FoWorld.tier.FoTier;
import net.minecraft.world.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import org.lwjgl.system.windows.WINDOWPLACEMENT;

import java.lang.reflect.Array;

public class ItemRegistry{
    public static final DeferredRegister<Item> ITEMS=
            DeferredRegister.create(ForgeRegistries.ITEMS,"foworld");

    //物品item
    //铁粉尘
    public static final RegistryObject<Item> IRON_DUST=
            ITEMS.register("iron_dust",()->
                    new Item(new Item.Properties()));

    public static final RegistryObject<Item> LUMINESCENT_DUST=
            ITEMS.register("luminescent_dust",()->
                    new Item(new Item.Properties()));

    //工具item
    //复合钻石剑
    public static final RegistryObject<SwordItem> SUPER_DIAMOND_SWORD =
            ITEMS.register("super_diamond_sword",() ->
                    new SwordItem(Tiers.DIAMOND,3,-2.3f,new Item.Properties())
            );

    //复合钻石镐
    public static final RegistryObject<PickaxeItem>SUPER_DIAMOND_PICKAXE=
            ITEMS.register("super_diamond_pickaxe",()->
                   new PickaxeItem(Tiers.DIAMOND,2,-2.7f,new Item.Properties())
            );

    //辉光钻石镐
    public static final RegistryObject<PickaxeItem> GLOW_DIAMOND_PICKAXE=GlowDiamondPickaxeItem.registry(ITEMS);
    //枯光石刃
public static final RegistryObject<SwordItem> WITHERED_GLIMMER_BLADE= WitheredGlimmerBlade.registry(ITEMS);
    //  ---重锤系列---
    // 木制重锤
    public static final RegistryObject<PickaxeItem>WOODEN_HEAVY_HAMMER=
            ITEMS.register("wooden_heavy_hammer",()->
                    new PickaxeItem(Tiers.WOOD,7,-3.4f,new Item.Properties().durability(102))
            );
    // 石制重锤
    public static final RegistryObject<PickaxeItem>STONE_HEAVY_HAMMER=
            ITEMS.register("stone_heavy_hammer",()->
                    new PickaxeItem(Tiers.STONE,8,-3.4f,new Item.Properties().durability(355))
            );
    public static final RegistryObject<PickaxeItem>IRON_HEAVY_HAMMER=
            ITEMS.register("iron_heavy_hammer",()->
                    new PickaxeItem(Tiers.IRON,8,-3.3f,new Item.Properties().durability(584))
            );


    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }



}