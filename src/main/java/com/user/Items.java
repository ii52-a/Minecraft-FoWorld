package com.user;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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


    // 用这个 Map 把注册后的对象存起来，方便以后随时调用
    public static final Map<String, RegistryObject<Item>> RUNE_MAP = new LinkedHashMap<>();

    public static void register(IEventBus eventBus){
        for (String path : RUNES) {
            String id = path.substring(path.lastIndexOf("/") + 1);

            // 关键：把 register 的返回值存入 Map
            RegistryObject<Item> obj = ITEMS.register(id, () -> new Item(new Item.Properties()));
            RUNE_MAP.put(id, obj);
        }
        ITEMS.register(eventBus);
    }
}
