package ii52.FoWorld.data;

import ii52.FoWorld.registry.RuneRegistry;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import java.util.Map;

public class FoLangProvider extends LanguageProvider {
    private final String locale;

    public FoLangProvider(PackOutput output, String locale) {
        super(output, "foworld", locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        if (this.locale.equals("zh_cn")) {
            addChinese();
        } else {
            addEnglish();
        }
    }

    private void addChinese() {
        add("item.foworld.super_diamond_sword", "复合钻石剑");
        add("item.foworld.super_diamond_pickaxe", "复合钻石镐");
        add("item.foworld.glow_diamond_pickaxe", "辉光钻石镐");
        add("item.foworld.withered_glimmer_blade", "枯光石刃");
        add("item.foworld.wooden_heavy_hammer", "木制重锤");
        add("item.foworld.stone_heavy_hammer", "石制重锤");
        add("item.foworld.iron_heavy_hammer", "铁制重锤");
        add("item.foworld.iron_dust", "铁粉尘");
        add("item.foworld.luminescent_dust", "荧光粉尘");
        add("item.foworld.glowing_steak", "闪光牛排");
        add("item.foworld.glowing_raw_beef", "调制闪光生牛肉");

        add("block.foworld.fo_bench", "世界工作台");
        add("block.foworld.lower_lever_crusher", "石制粉碎机");
        add("block.foworld.luminex_flower", "荧蜕花");
        add("block.foworld.glow_tombstone", "荧光墓碑");
        add("block.foworld.chiseled_glow_vein_quartz", "鉴刻光纹石英");
        add("block.foworld.deep_orchid", "伶幽兰");
        add("block.foworld.fluorite_ore", "荧光石");
        add("block.foworld.fluorite_ore_0", "荧光态微石");
        add("block.foworld.fluorite_ore_1", "荧光微石");
        add("block.foworld.deep_glow_stone", "暗光荧石");
        add("block.foworld.glow_altar", "辉光祭坛");
        add("block.foworld.light_veined_stone", "熠光石台");

        add("block.foworld.skylight_log", "天光木");
        add("block.foworld.skylight_wood", "天光木块");
        add("block.foworld.skylight_sapling", "天光树苗");
        add("block.foworld.glow_log", "荧光木");
        add("block.foworld.glow_wood", "荧光木块");

        add("effect.foworld.glow_poison", "闪光中毒");

        add("biome.foworld.skylight_forest", "天光森林");

        add("itemGroup.foworld.main_tab", "FoWorld 未来之征");

        Map<String, String> runes = Map.of(
                "azure_rune_stone", "天蔚符石",
                "morning_glow_rune_stone", "霞光符石",
                "qi_zhao", "启兆符石",
                "twilight_purple_rune_stone", "幽紫符石",
                "vast_sea_rune", "沧海符石",
                "verdant_rune", "青翠符石",
                "whitespace_stone", "空白符石"
        );
        runes.forEach((id, name) -> add("item.foworld." + id, name));
    }

    private void addEnglish() {
        add("item.foworld.super_diamond_sword", "Composite Diamond Sword");
        add("item.foworld.super_diamond_pickaxe", "Composite Diamond Pickaxe");
        add("item.foworld.glow_diamond_pickaxe", "Glow Diamond Pickaxe");
        add("item.foworld.luminescent_dust", "Luminescent Dust");
        add("item.foworld.iron_dust", "Iron Dust");
        add("item.foworld.wooden_heavy_hammer", "Wooden Heavy Hammer");
        add("item.foworld.stone_heavy_hammer", "Stone Heavy Hammer");
        add("item.foworld.iron_heavy_hammer", "Iron Heavy Hammer");
        add("item.foworld.withered_glimmer_blade", "Withered Glimmer Blade");
        add("item.foworld.glowing_steak", "Glowing Steak");
        add("item.foworld.glowing_raw_beef", "Glowing Raw Beef");

        add("block.foworld.fo_bench", "FoWorld Crafting Table");
        add("block.foworld.lower_lever_crusher", "Stone Crusher");
        add("block.foworld.luminex_flower", "Luminex Flower");
        add("block.foworld.deep_orchid", "Deep Orchid");
        add("block.foworld.glow_tombstone", "Glow Tombstone");
        add("block.foworld.chiseled_glow_vein_quartz", "Chiseled Glow Vein Quartz");
        add("block.foworld.fluorite_ore", "Fluorite Ore");
        add("block.foworld.fluorite_ore_0", "Fluorite Ore Mid 0");
        add("block.foworld.fluorite_ore_1", "Fluorite Ore Mid 1");
        add("block.foworld.deep_glow_stone", "Deep Glow Stone");
        add("block.foworld.light_veined_stone", "Light Veined Stone");
        add("block.foworld.glow_altar", "Glow Altar");

        add("block.foworld.skylight_log", "Skylight Log");
        add("block.foworld.skylight_wood", "Skylight Wood");
        add("block.foworld.skylight_sapling", "Skylight Sapling");
        add("block.foworld.glow_log", "Glow Log");
        add("block.foworld.glow_wood", "Glow Wood");

        add("effect.foworld.glow_poison", "Glow Poison");

        add("biome.foworld.skylight_forest", "Skylight Forest");

        add("creativetab.foworld_tab", "FoWorld: Future Journey");

        for (String path : RuneRegistry.RUNE_PATHS) {
            String id = path.substring(path.lastIndexOf("/") + 1);
            add("item.foworld." + id, id.replace("_", " ").toUpperCase());
        }
    }
}
