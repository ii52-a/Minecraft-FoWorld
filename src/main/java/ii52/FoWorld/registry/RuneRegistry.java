package ii52.FoWorld.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RuneRegistry {
    public static final List<String> RUNE_PATHS = List.of(
            "rune/azure_rune_stone",
            "rune/morning_glow_rune_stone",
            "rune/qi_zhao",
            "rune/twilight_purple_rune_stone",
            "rune/vast_sea_rune",
            "rune/verdant_rune",
            "rune/whitespace_stone"
    );

    public static final DeferredRegister<Item> RUNE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "foworld");

    public static final Map<String, RegistryObject<Item>> RUNE_MAP = new LinkedHashMap<>();

    public static void register(IEventBus eventBus) {
        for (String path : RUNE_PATHS) {
            String id = path.substring(path.lastIndexOf("/") + 1);
            RegistryObject<Item> obj = RUNE_ITEMS.register(id, () -> new Item(new Item.Properties()));
            RUNE_MAP.put(id, obj);
        }
        RUNE_ITEMS.register(eventBus);
    }
}
