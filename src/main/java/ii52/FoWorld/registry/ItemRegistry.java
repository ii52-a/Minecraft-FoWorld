package ii52.FoWorld.registry;

import net.minecraft.world.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

public class ItemRegistry{
    public static final DeferredRegister<Item> ITEMS=
            DeferredRegister.create(ForgeRegistries.ITEMS,"foworld");

    public static final RegistryObject<SwordItem> SUPER_DIAMOND_SWORD =
            ITEMS.register("super_diamond_sword",() ->
                    new SwordItem(Tiers.DIAMOND,8,-2.4f,new Item.Properties().fireResistant())
            );

    public static final RegistryObject<PickaxeItem>SUPER_DIAMOND_PICKAXE=
            ITEMS.register("super_diamond_pickaxe",()->
                   new PickaxeItem(Tiers.DIAMOND,2,-1.7f,new Item.Properties().fireResistant())
            );


    public static void register(IEventBus bus){
        ITEMS.register(bus);
    }



}