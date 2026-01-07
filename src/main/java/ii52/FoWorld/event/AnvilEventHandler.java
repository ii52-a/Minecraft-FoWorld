package ii52.FoWorld.event; // 确保包名正确

import ii52.FoWorld.ModTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = "foworld")
public class AnvilEventHandler {


    // 第二道门：物理取出拦截（防止客户端和服务端不同步导致的强行取出）
    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack output = event.getOutput();
        if (output.is(ModTags.Items.NON_RENAMEABLE)) {
            // 工业模组的逻辑：检测如果产物带有 CustomName，则强行移除它
            // 这样玩家拿出来后，名字会自动跳回原样
            output.removeTagKey("display");

        }
    }
}