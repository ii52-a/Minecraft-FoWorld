package ii52.FoWorld.event;

import com.user.LangProvider;
import com.user.SimpleItemDatagen;
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

        // 注册你的模型生成器

        // 这里就是“挂载”！
        // 1. 生成英文翻译文件
        generator.addProvider(event.includeClient(), new LangProvider(output, "en_us"));

        // 2. 生成中文翻译文件
        generator.addProvider(event.includeClient(), new LangProvider(output, "zh_cn"));

        // 3. 顺便把你之前的模型生成 (Datagen) 也挪到这里来，整齐一点
        generator.addProvider(event.includeClient(), new SimpleItemDatagen(output, existingFileHelper));

    }
}