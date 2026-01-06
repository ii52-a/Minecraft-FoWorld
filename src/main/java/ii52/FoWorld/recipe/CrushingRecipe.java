package ii52.FoWorld.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import java.util.List;
import net.minecraft.world.SimpleContainer;
/**
 * 粉碎机配方类 (CrushingRecipe)
 * 这个类就像是一张“说明书”，告诉机器：
 * 1. 需要什么原材料 (input)
 * 2. 必定产出什么 (output)
 * 3. 概率产出哪些副产物 (secondaryResults)
 */
public class CrushingRecipe implements Recipe<SimpleContainer> {

    // --- 1. 配方核心数据 ---
    public final ResourceLocation id;              // 配方的身份证号 (例如 foworld:iron_crushing)
    public final ItemStack output;                 // 主产物 (必定掉落的)
    public final Ingredient input;                 // 输入原料 (可以是某个物品，也可以是某个标签)
    public final List<SecondaryResult> secondaryResults; // 副产物清单 (列表结构，支持多个)

    /**
     * 副产物记录类 (Record)
     * Java 16+ 的新特性，专门用来存放这种简单的“数据对”。
     * stack: 物品堆 (物品 + 数量)
     * chance: 触发概率 (0.0F ~ 1.0F)
     */
    public record SecondaryResult(ItemStack stack, float chance) {}

    /**
     * 构造函数：由 Serializer (序列化器) 调用，把从 JSON 读到的数据塞进这个对象里。
     */
    public CrushingRecipe(ResourceLocation id, ItemStack output, Ingredient input, List<SecondaryResult> secondaryResult) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.secondaryResults = secondaryResult;
    }

    /**
     * 匹配逻辑：机器怎么知道我放进去的东西对不对？
     * @param container 机器的物品容器 (我们只检查 0 号输入槽)
     * @param level 当前世界
     * @return 如果原料匹配，返回 true，机器开始涨进度。
     */
    @Override
    public boolean matches(SimpleContainer container, Level level) {
        // 原理：拿配方要求的 ingredient 去测试容器里第 0 个格子的物品
        return input.test(container.getItem(0));
    }

    /**
     * 结果合成：当进度条满了，最后要生成什么物品堆？
     * 注意：这里通常只返回主产物，副产物逻辑由 BlockEntity 里的 executeCrush 手动处理。
     */
    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        // 必须用 .copy()，否则会直接引用原始堆导致数据错乱
        return output.copy();
    }





    /**
     * 兼容性检查：是否能适配某种尺寸的合成表？
     * 对于粉碎机这种单槽机器，通常直接返回 true 即可。
     */
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    /**
     * 获取结果：主要用于 JEI 等模组显示配方结果。
     */
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output;
    }

    /**
     * 副产物 Getter：供 BlockEntity 里的循环逻辑使用，用来判定概率掉落。
     */
    public List<SecondaryResult> getSecondaryResults() {
        return secondaryResults;
    }

    /**
     * 返回此配方的 ResourceLocation ID。
     */
    @Override
    public ResourceLocation getId() {
        return id;
    }

    /**
     * 绑定序列化器：告诉游戏，谁负责把这个类转成 JSON 或者是发往网络。
     */
    @Override
    public RecipeSerializer<?> getSerializer() {
        // 引用我们之前在 RecipeRegistry 里注册好的那个 Serializer
        return ii52.FoWorld.registry.RecipeRegistry.CRUSHING_SERIALIZER.get();
    }

    /**
     * 绑定配方类型：告诉游戏，这属于哪种合成动作（粉碎、烧炼、合成等）。
     */
    @Override
    public RecipeType<?> getType() {
        // 引用我们之前在 RecipeRegistry 里注册好的那个 Type
        return ii52.FoWorld.registry.RecipeRegistry.CRUSHING_TYPE.get();
    }
}