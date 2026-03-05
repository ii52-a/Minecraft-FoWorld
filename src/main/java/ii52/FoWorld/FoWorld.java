package ii52.FoWorld;

import com.mojang.logging.LogUtils;
import ii52.FoWorld.block.skylight.SkylightRegistry;
import ii52.FoWorld.registry.*;
import ii52.FoWorld.worldgen.SkylightFeatures;
import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
import ii52.FoWorld.registry.BlockRegistry.BlockRegistry;
import ii52.FoWorld.registry.FlowerRegistry.FlowerEntityRegistry;
import ii52.FoWorld.registry.FlowerRegistry.FlowerRegistry;
import ii52.FoWorld.screen.FoBenchScreen;
import ii52.FoWorld.screen.LowerLeverCrusherScreen;
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

@Mod(FoWorld.MODID)
public class FoWorld
{
    public static final String MODID = "foworld";
    private static final Logger LOGGER = LogUtils.getLogger();

    public FoWorld(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        BlockRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);
        EntityRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);

        FlowerEntityRegistry.register(modEventBus);
        FlowerRegistry.register(modEventBus);

        RecipeRegistry.TYPES.register(modEventBus);
        RecipeRegistry.SERIALIZERS.register(modEventBus);

        EffectRegistry.register(modEventBus);

        RuneRegistry.register(modEventBus);

        SkylightRegistry.register(modEventBus);

        SkylightFeatures.FEATURES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        CreativeTabRegistry.TABS.register(modEventBus);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("通用设置已启动");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("服务器已成功开启");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                MenuScreens.register(MenuRegistry.FO_BENCH_MENU.get(), FoBenchScreen::new);
            });
            event.enqueueWork(() -> {
                MenuScreens.register(MenuRegistry.LOWER_LEVER_CRUSHER.get(), LowerLeverCrusherScreen::new);
            });
        }
    }
}
