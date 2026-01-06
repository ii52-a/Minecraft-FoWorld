package ii52.FoWorld.registry;

import ii52.FoWorld.menu.FoBenchMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType; // 核心：Forge提供的扩展
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, "foworld");

    // 使用 IForgeMenuType.create，它可以接收 (windowId, inv, extraData) 三个参数
    public static final RegistryObject<MenuType<FoBenchMenu>> FO_BENCH_MENU =
            MENUS.register("fo_bench",
                    () -> IForgeMenuType.create(FoBenchMenu::new)
            );
    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}