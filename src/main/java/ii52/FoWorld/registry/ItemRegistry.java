package ii52.FoWorld.registry;

import ii52.FoWorld.item.GlowDiamondPickaxeItem;
import net.minecraft.world.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

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
                    new SwordItem(Tiers.DIAMOND,4,-2.4f,new Item.Properties())
            );

    //复合钻石镐
    public static final RegistryObject<PickaxeItem>SUPER_DIAMOND_PICKAXE=
            ITEMS.register("super_diamond_pickaxe",()->
                   new PickaxeItem(Tiers.DIAMOND,2,-2.7f,new Item.Properties())
            );

    //辉光钻石镐
    public static final RegistryObject<PickaxeItem> GLOW_DIAMOND_PICKAXE=
            ITEMS.register("glow_diamond_pickaxe",() ->
                    new GlowDiamondPickaxeItem(new Item.Properties())
            );
    public static final RegistryObject<PickaxeItem> GLOW_DIAMOND_PICKAXE_ACTIVE=
            ITEMS.register("glow_diamond_pickaxe_active",() ->
                    new PickaxeItem(Tiers.DIAMOND,2,-2.7f,new Item.Properties())
            );

    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }



}