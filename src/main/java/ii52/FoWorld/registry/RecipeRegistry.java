package ii52.FoWorld.registry;
import ii52.FoWorld.recipe.CrushingRecipe;
import ii52.FoWorld.recipe.FoBenchShapedRecipe;
import ii52.FoWorld.recipe.FoBenchShapelessRecipe;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import ii52.FoWorld.Serializer.CurshingSerializer;
import net.minecraftforge.registries.*;
public class RecipeRegistry{
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "foworld");
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "foworld");

    public static final RegistryObject<RecipeSerializer<FoBenchShapedRecipe>> FO_BENCH_SHAPED_SERIALIZER =
            SERIALIZERS.register("fo_bench_shaped", FoBenchShapedRecipe.Serializer::new);

    // 2. 注册配方类型
    public static final RegistryObject<RecipeType<FoBenchShapedRecipe>> FO_BENCH_TYPE =
            TYPES.register("fo_bench_recipe", () -> new RecipeType<FoBenchShapedRecipe>() {
                @Override
                public String toString() { return "foworld:fo_bench_recipe"; }
            });
    // 注册序列化器（负责把 JSON 变成 Java 对象）
    public static final RegistryObject<RecipeSerializer<CrushingRecipe>> CRUSHING_SERIALIZER =
            SERIALIZERS.register("crushing", CurshingSerializer::new);

    public static final RegistryObject<RecipeSerializer<?>> FO_BENCH_SHAPELESS_SERIALIZER =
            SERIALIZERS.register("fo_bench_shapeless", FoBenchShapelessRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<CrushingRecipe>> CRUSHING_TYPE =
            TYPES.register("crushing", () -> new RecipeType<>() {
                @Override
                public String toString() { return "foworld:crushing"; }
            });


}