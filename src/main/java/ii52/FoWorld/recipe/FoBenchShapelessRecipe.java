package ii52.FoWorld.recipe;

import ii52.FoWorld.registry.RecipeRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class FoBenchShapelessRecipe implements CraftingRecipe {
    // 【核心改动】这里代理的是 ShapelessRecipe (无序)
    private final ShapelessRecipe internal;

    public FoBenchShapelessRecipe(ShapelessRecipe internal) {
        this.internal = internal;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.FO_BENCH_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        // 这里要指向你新注册的无序序列化器
        return RecipeRegistry.FO_BENCH_SHAPELESS_SERIALIZER.get();
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        // 无序匹配逻辑：internal 会自动检查物品清单是否一致，不管位置
        if (!internal.matches(inv, level)) return false;

        // 同样的耐久检测
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isDamageableItem() && stack.getDamageValue() > 0) {
                return false;
            }
        }
        return true;
    }

    // --- 转发逻辑 (与 Shaped 类似) ---
    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess access) { return internal.assemble(inv, access); }
    @Override
    public boolean canCraftInDimensions(int width, int height) { return internal.canCraftInDimensions(width, height); }
    @Override
    public ItemStack getResultItem(RegistryAccess access) { return internal.getResultItem(access); }
    @Override
    public NonNullList<Ingredient> getIngredients() { return internal.getIngredients(); }
    @Override
    public ResourceLocation getId() { return internal.getId(); }
    @Override
    public CraftingBookCategory category() { return CraftingBookCategory.MISC; }

    // --- 序列化器 (必须借用 SHAPELESS_RECIPE) ---
    public static class Serializer implements RecipeSerializer<FoBenchShapelessRecipe> {
        @Override
        public FoBenchShapelessRecipe fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
            // 【关键】这里必须调用原版的 SHAPELESS_RECIPE 解析器
            ShapelessRecipe original = RecipeSerializer.SHAPELESS_RECIPE.fromJson(id, json);
            return new FoBenchShapelessRecipe(original);
        }

        @Override
        public FoBenchShapelessRecipe fromNetwork(ResourceLocation id, net.minecraft.network.FriendlyByteBuf buf) {
            return new FoBenchShapelessRecipe(RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(id, buf));
        }

        @Override
        public void toNetwork(net.minecraft.network.FriendlyByteBuf buf, FoBenchShapelessRecipe recipe) {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buf, recipe.internal);
        }
    }
}