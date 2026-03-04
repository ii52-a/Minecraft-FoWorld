package ii52.FoWorld.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import java.util.List;
import java.util.ArrayList;

public class GlowAltarRecipe implements Recipe<SimpleContainer> {

    public final ResourceLocation id;
    public final List<Ingredient> inputs;
    public final ItemStack output;
    public final int processingTime;

    public GlowAltarRecipe(ResourceLocation id, List<Ingredient> inputs, ItemStack output, int processingTime) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.processingTime = processingTime;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        if (items.size() != inputs.size()) {
            return false;
        }
        List<Ingredient> remainingIngredients = new ArrayList<>(inputs);
        for (ItemStack stack : items) {
            boolean found = false;
            for (int i = 0; i < remainingIngredients.size(); i++) {
                if (remainingIngredients.get(i).test(stack)) {
                    remainingIngredients.remove(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return remainingIngredients.isEmpty();
    }

    @Override
    public ItemStack assemble(SimpleContainer container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public List<Ingredient> getInputs() {
        return inputs;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ii52.FoWorld.registry.RecipeRegistry.GLOW_ALTAR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ii52.FoWorld.registry.RecipeRegistry.GLOW_ALTAR_TYPE.get();
    }
}
