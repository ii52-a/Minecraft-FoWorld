package ii52.FoWorld.recipe;

import ii52.FoWorld.registry.RecipeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

/**
 * 这是一个“伪装”成 CraftingRecipe 的类。
 * 为什么要实现接口而不是继承 ShapedRecipe？
 * 1. 彻底断绝血缘关系：防止原版合成书通过 instanceof ShapedRecipe 找到并显示它。
 * 2. 避免方法冲突：ShapedRecipe 内部有很多私有方法和复杂的枚举返回值，直接继承会引发编译错误。
 */
public class FoBenchShapedRecipe implements CraftingRecipe {

    // 【代理模式核心】内部偷偷藏着一个真正的原版 ShapedRecipe。
    // 我们自己不写九宫格坐标匹配算法，因为 Mojang 已经写得很完美了（支持镜像、旋转、偏移）。
    private final ShapedRecipe internal;

    public FoBenchShapedRecipe(ShapedRecipe internal) {
        this.internal = internal;
    }

    // --- 1. 身份标记（这是最关键的部分） ---

    @Override
    public RecipeType<?> getType() {
        // 【核心隔离】原版工作台只找 RecipeType.CRAFTING。
        // 我们返回自己注册的 FO_BENCH_TYPE，原版工作台就会因为“不属于我的工作范畴”而无视它。
        return RecipeRegistry.FO_BENCH_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // 返回我们自己的解析器，这样游戏才知道这个配方应该用我们这个类来创建。
        return RecipeRegistry.FO_BENCH_SHAPED_SERIALIZER.get();
    }

    @Override
    public boolean isSpecial() {
        // 标记为特殊配方：系统在处理自动合成、合成书预览时会更加谨慎。
        return true;
    }

    // --- 2. 逻辑转发（把脏活累活丢给 internal 对象） ---

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        // 核心匹配逻辑：直接问 internal “这九个格子摆的形状对不对？”
        if (!internal.matches(inv, level)){
            return false;
        }
        for (int i=0; i<inv.getContainerSize();i++){
            ItemStack stack=inv.getItem(i);
            if (stack.isDamageableItem()){
                if (stack.getDamageValue() >0){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) {
        // 结果产出逻辑：直接问 internal “这九个格子合出来应该是什么？”
        return internal.assemble(inv, access);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        // 容器大小检查：直接问 internal “3x3 的格子放得下吗？”
        return internal.canCraftInDimensions(width, height);
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        // 获取预设产物（用于 JEI 或预览）
        return internal.getResultItem(access);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        // 获取所需的材料列表（用于 JEI 展示）
        return internal.getIngredients();
    }

    @Override
    public ResourceLocation getId() {
        // 返回配方的注册 ID（如 foworld:super_diamond_pickaxe）
        return internal.getId();
    }

    @Override
    public CraftingBookCategory category() {
        // 必须实现，虽然由于 Type 不同它不会进合成书，但这里返回 MISC（杂项）是最稳妥的。
        return CraftingBookCategory.MISC;
    }

    // --- 3. 翻译官（序列化器） ---

    public static class Serializer implements RecipeSerializer<FoBenchShapedRecipe> {
        /**
         * 游戏启动时，当看到 JSON 里的 type 是 foworld:fo_bench_shaped 时调用。
         */
        @Override
        public FoBenchShapedRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            // 我们不写 JSON 解析代码。
            // 直接借用原版 ShapedRecipe 的解析器，把 JSON 变成一个原版对象。
            ShapedRecipe original = RecipeSerializer.SHAPED_RECIPE.fromJson(id, json);
            // 然后把这个原版对象“打包”进我们的外壳类里。
            return new FoBenchShapedRecipe(original);
        }

        /**
         * 联机时，服务端把配方数据发送给客户端时调用。
         */
        @Override
        public FoBenchShapedRecipe fromNetwork(ResourceLocation id, net.minecraft.network.FriendlyByteBuf buf) {
            return new FoBenchShapedRecipe(RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buf));
        }

        /**
         * 联机时，客户端接收到数据进行写入时调用。
         */
        @Override
        public void toNetwork(net.minecraft.network.FriendlyByteBuf buf, FoBenchShapedRecipe recipe) {
            // 写入时，要把包装在里面的 internal 对象取出来写进网络包。
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buf, recipe.internal);
        }
    }
}