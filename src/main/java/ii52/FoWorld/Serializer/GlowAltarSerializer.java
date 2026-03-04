package ii52.FoWorld.Serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ii52.FoWorld.recipe.GlowAltarRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class GlowAltarSerializer implements RecipeSerializer<GlowAltarRecipe> {

    @Override
    public GlowAltarRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
        JsonArray ingredientsArray = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
        List<Ingredient> inputs = new ArrayList<>();
        for (JsonElement element : ingredientsArray) {
            inputs.add(Ingredient.fromJson(element));
        }

        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"));

        int processingTime = GsonHelper.getAsInt(pSerializedRecipe, "processingTime", 200);

        return new GlowAltarRecipe(pRecipeId, inputs, result, processingTime);
    }

    @Override
    public GlowAltarRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
        int size = pBuffer.readInt();
        List<Ingredient> inputs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            inputs.add(Ingredient.fromNetwork(pBuffer));
        }

        ItemStack output = pBuffer.readItem();
        int processingTime = pBuffer.readInt();

        return new GlowAltarRecipe(pRecipeId, inputs, output, processingTime);
    }

    @Override
    public void toNetwork(FriendlyByteBuf pBuffer, GlowAltarRecipe pRecipe) {
        pBuffer.writeInt(pRecipe.inputs.size());
        for (Ingredient ingredient : pRecipe.inputs) {
            ingredient.toNetwork(pBuffer);
        }

        pBuffer.writeItem(pRecipe.output);
        pBuffer.writeInt(pRecipe.processingTime);
    }
}
