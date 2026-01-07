package ii52.FoWorld.client.item; // 声明包名，通常放在 client 包下，因为模型处理属于客户端逻辑

// 导入必要的类
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

                // 参数 2: 属性的名称（ResourceLocation）
                // 这个名字必须和你物品 json 文件中 "overrides" 部分的 "predicate" 名字对应
                new ResourceLocation("foworld", "glowing"),

                // 参数 3: 属性值的逻辑判断（Lambda 表达式）
                // stack: 当前物品堆叠, level: 当前世界, entity: 持有该物品的实体, seed: 随机种子
                (stack, level, entity, seed) -> {
                    // 判断逻辑：
                    // 1. stack.hasTag(): 检查物品是否有 NBT 数据
                    // 2. stack.getTag().getBoolean("glowing"): 读取名为 "glowing" 的布尔值
                    // 如果 NBT 里的 "glowing" 为 true，则返回 1.0F，否则返回 0.0F
                    return stack.hasTag() && stack.getTag().getBoolean("glowing") ? 1.0F : 0.0F;
                }
        );
    }
}