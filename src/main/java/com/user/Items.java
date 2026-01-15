package com.user;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class Items {
    public static final List<String> RUNES = List.of(
            "rune/azure_rune_stone",
            "rune/morning_glow_rune_stone",
            "rune/qi_zhao",
            "rune/twilight_purple_rune_stone",
            "rune/vast_sea_rune",
            "rune/verdant_rune",
            "rune/whitespace_stone"
    );


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "foworld");

    // 自动化注册
    public static void register(IEventBus eventBus){
        for (String path : Items.RUNES) {

            String id = path.substring(path.lastIndexOf("/") + 1);

            ITEMS.register(id, () -> new Item(new Item.Properties()));

        }
        ITEMS.register(eventBus);
    }
}
