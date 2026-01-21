package ii52.FoWorld.client;

import ii52.FoWorld.FoWorld;
import ii52.FoWorld.client.item.ModItemProperties;
import ii52.FoWorld.registry.EntityRegistry;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = FoWorld.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModItemProperties.register();
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // 告诉游戏：当你看到 GLOW_TNT 实体时，使用原版的 TntRenderer 来画它
        // TntRenderer 会自动根据你方块的贴图来渲染实体
        event.registerEntityRenderer(EntityRegistry.GLOW_TNT.get(), TntRenderer::new);
    }
}