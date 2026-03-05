package ii52.FoWorld.event;

import ii52.FoWorld.data.FoItemModelProvider;
import ii52.FoWorld.data.FoLangProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "foworld", bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataEvent {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new FoLangProvider(output, "en_us"));
        generator.addProvider(event.includeClient(), new FoLangProvider(output, "zh_cn"));
        generator.addProvider(event.includeClient(), new FoItemModelProvider(output, existingFileHelper));
    }
}
