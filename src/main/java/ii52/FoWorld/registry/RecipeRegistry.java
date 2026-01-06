package ii52.FoWorld.registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.*;
public class RecipeRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "foworld");
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, "foworld");

    public static final RegistryObject<RecipeSerializer<ShapedRecipe>> FO_BENCH_SHAPED_SERIALIZER =
            SERIALIZERS.register("fo_bench_shaped", ShapedRecipe.Serializer::new);

    // 2. 注册配方类型
    public static final RegistryObject<RecipeType<ShapedRecipe>> FO_BENCH_TYPE =
            TYPES.register("fo_bench_recipe", () -> new RecipeType<ShapedRecipe>() {
                @Override
                public String toString() { return "foworld:fo_bench_recipe"; }
            });
}