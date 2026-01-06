package ii52.FoWorld;

import com.mojang.logging.LogUtils;
import ii52.FoWorld.registry.*;
import ii52.FoWorld.screen.FoBenchScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Mod 主类。@Mod 注解必须匹配 mods.toml 中的 modId。
 */
@Mod(FoWorld.MODID)
public class FoWorld
{
    public static final String MODID = "foworld";
    private static final Logger LOGGER = LogUtils.getLogger();


    /**
     * 构造函数：这是 Mod 启动时第一个跑的地方。
     */
    public FoWorld(FMLJavaModLoadingContext context)
    {
        // 获取事件总线（Event Bus），它是 Mod 内部沟通的“频道”
        IEventBus modEventBus = context.getModEventBus();

        // 【关键】把你之前写的那些“注册表”类全部挂载到总线上
        // 这一步如果不做，你写的那些 .register(...) 代码永远不会被执行
        BlockRegistry.register(modEventBus);       // 注册方块
        ItemRegistry.register(modEventBus);        // 注册物品
        BlockEntityRegistry.register(modEventBus); // 注册方块实体
        MenuRegistry.register(modEventBus);        // 注册 GUI 菜单逻辑

        RecipeRegistry.TYPES.register(modEventBus);
        RecipeRegistry.SERIALIZERS.register(modEventBus);

        // 注册一些生命周期监听（比如通用启动、加入物品栏等）
        modEventBus.addListener(this::commonSetup);


        // MinecraftForge.EVENT_BUS 是全局总线，用于监听游戏内的事件（如玩家进服、方块破坏等）
        MinecraftForge.EVENT_BUS.register(this);


        // 注册配置文件（Config）
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // 这里写一些不分客户端还是服务器都要跑的逻辑（比如初始化一些通用设置）
        LOGGER.info("通用设置已启动");
    }

    /**
     * 物品栏注册逻辑
     */

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // 服务器启动时跑的代码
        LOGGER.info("服务器已成功开启");
    }

    /**
     * 【极其关键】客户端专用事件
     * 只有当你运行客户端游戏（而不是开服务器）时，这段代码才会跑。
     * 所有和 UI、渲染、贴图有关的代码必须写在这里。
     */
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // 解决“界面点不开”的核心代码：
            // 将 Menu（后端逻辑）和 Screen（前端贴图类）绑定在一起。
            // 以后每做一个新的工作台，都要在这里加一行 MenuScreens.register。
            event.enqueueWork(() -> {
                MenuScreens.register(MenuRegistry.FO_BENCH_MENU.get(), FoBenchScreen::new);
            });
        }
    }
}