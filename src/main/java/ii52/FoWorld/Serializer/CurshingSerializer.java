package ii52.FoWorld.Serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ii52.FoWorld.recipe.CrushingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;


import java.util.ArrayList;
import java.util.List;

public class CurshingSerializer implements RecipeSerializer<CrushingRecipe> {

    /**
     * 场景 A：从 JSON 文件读取配方 (服务端启动时调用)
     * @param pRecipeId 配方的 ID (比如 foworld:iron_crushing)
     * @param pSerializedRecipe 整个 JSON 对象的内容
     */
    @Override
    public CrushingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        // 1. 解析“输入物品” (Ingredient)
        // 使用 GsonHelper 安全地获取 JSON 里的 "ingredient" 节点
        // Ingredient.fromJson 是原版自带的方法，能处理单物品、标签(Tag)或列表
        Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "ingredient"));

        // 2. 解析“主产物” (Result)
        // ShapedRecipe.itemStackFromJson 是一个非常方便的工具方法
        // 它能解析类似 { "item": "minecraft:iron_ingot", "count": 2 } 的结构
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

        // 3. 解析“副产物” (Secondary Output) - 这一步带逻辑判断

        float chance = 0f;                     // 默认概率为 0

        // 检查 JSON 里是否有 "secondaries" 这个键
        List<CrushingRecipe.SecondaryResult> secondaries = new ArrayList<>();
        if (pSerializedRecipe.has("secondaries")) {
            JsonArray array = GsonHelper.getAsJsonArray(pSerializedRecipe, "secondaries");
            for (JsonElement element : array) {
                JsonObject obj = element.getAsJsonObject();
                ItemStack stack = ShapedRecipe.itemStackFromJson(obj);
                chance = GsonHelper.getAsFloat(obj, "chance");
                secondaries.add(new CrushingRecipe.SecondaryResult(stack, chance));
            }
        }

        // 返回一个根据 JSON 数据创建的 Java 配方对象
        return new CrushingRecipe(pRecipeId, result, input, secondaries);
    }

    /**
     * 场景 B：从网络读取配方 (客户端连接服务器时调用)
     * 为了保证联机时配方一致，服务器会把自己的配方“广播”给玩家的电脑
     * 这里的读取顺序必须和下面的 toNetwork 写入顺序完全一致
     */
    @Override
    public CrushingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        Ingredient input = Ingredient.fromNetwork(pBuffer);
        ItemStack output = pBuffer.readItem();

        // 读取长度并循环恢复 List
        int size = pBuffer.readInt();
        List<CrushingRecipe.SecondaryResult> secondaries = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            secondaries.add(new CrushingRecipe.SecondaryResult(pBuffer.readItem(), pBuffer.readFloat()));
        }
        return new CrushingRecipe(pRecipeId, output, input, secondaries);
    }

    /**
     * 场景 C：写入网络数据包 (服务端发送给客户端时调用)
     * 将 Java 对象的变量转换成二进制流发送出去
     */
    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, CrushingRecipe pRecipe) {
        // 1. 将输入物品写入包
        pRecipe.input.toNetwork(pBuffer);
        // 2. 将主产物物品堆写入包
        pBuffer.writeItem(pRecipe.output);
        pBuffer.writeInt(pRecipe.secondaryResults.size());
        for (CrushingRecipe.SecondaryResult sr : pRecipe.secondaryResults) {
            pBuffer.writeItem(sr.stack());
            pBuffer.writeFloat(sr.chance());
        }
    }
}