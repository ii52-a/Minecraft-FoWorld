package ii52.FoWorld.client.item; // 声明包名，通常放在 client 包下，因为模型处理属于客户端逻辑

// 导入必要的类
import ii52.FoWorld.registry.BlockRegistry.BlockRegistry;
import ii52.FoWorld.registry.ItemRegistry; // 你自己的物品注册表
import net.minecraft.client.renderer.item.ItemProperties; // 原生用于处理物品属性的类
import net.minecraft.resources.ResourceLocation; // 用于定位资源的 ID（命名空间:路径）

/**
 * 这个类专门用于管理物品的特殊属性（例如：弓的拉满进度、盾牌是否举起、或者你自定义的 NBT 状态）
 */
public class ModItemProperties {

    // 定义一个静态注册方法，需要在客户端初始化（ClientSetup 事件）时调用
    public static void register() {

        // 调用 Minecraft 原生的注册方法
        ItemProperties.register(
                // 参数 1: 要绑定的物品实例
                ItemRegistry.GLOW_DIAMOND_PICKAXE.get(),


                new ResourceLocation("foworld", "glowing"),

                // 参数 3: 属性值的逻辑判断（Lambda 表达式）
                // stack: 当前物品堆叠, level: 当前世界, entity: 持有该物品的实体, seed: 随机种子
                (stack, level, entity, seed) -> {

                    return stack.hasTag() && stack.getTag().getBoolean("glowing") ? 1.0F : 0.0F;
                }
        );
        ItemProperties.register(ItemRegistry.WITHERED_GLIMMER_BLADE.get(),
                new ResourceLocation("foworld", "attract_count"),
                (stack, level, entity, seed) -> {
                    // 获取 NBT 计数，如果没有则返回 0
                    int count = stack.hasTag() ? stack.getTag().getInt("attract_count") : 0;

                    // 将计数映射为 JSON 可识别的数值（例如：0, 1, 2, 3）
                    // 提示：你可以直接返回 count，或者返回 count / 3.0F 这种百分比
                    return (float) count;
                }
        );
    }
}