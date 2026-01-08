//package ii52.FoWorld.event;
//
//import ii52.FoWorld.registry.FlowerRegistry.FlowerRegistry;
//import net.minecraft.client.renderer.ItemBlockRenderTypes;
//import net.minecraft.client.renderer.RenderType;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//
//// 使用 @Mod.EventBusSubscriber 自动挂载
//@Mod.EventBusSubscriber(modid = "foworld", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
//public class ClientModEvents {
//
//    @SubscribeEvent
//    public static void onClientSetup(FMLClientSetupEvent event) {
//        // 这一步是让游戏知道：这朵花不是实心的，请处理它的透明像素（Cutout）
//        // event.enqueueWork 是为了确保它是线程安全的
//        event.enqueueWork(() -> {
//            ItemBlockRenderTypes.setRenderLayer(FlowerRegistry.LUMINEX_FLOWER.get(), RenderType.cutout());
//        });
//    }
//}