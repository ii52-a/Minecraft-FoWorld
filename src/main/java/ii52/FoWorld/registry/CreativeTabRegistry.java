package ii52.FoWorld.registry;

import ii52.FoWorld.FoWorld;
import ii52.FoWorld.registry.BlockRegistry.BlockRegistry;
import ii52.FoWorld.registry.FlowerRegistry.FlowerRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabRegistry {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FoWorld.MODID);

    public static final RegistryObject<CreativeModeTab> FOWORLD_TAB =
            TABS.register("foworld_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("FoWorld未来之征"))
                            .icon(() -> new ItemStack(BlockRegistry.FO_BENCH.get()))
                            .displayItems((parameters, output) -> {

                                // 工具
                                output.accept(ItemRegistry.GLOW_DIAMOND_PICKAXE.get());
                                output.accept(ItemRegistry.SUPER_DIAMOND_PICKAXE.get());
                                output.accept(ItemRegistry.SUPER_DIAMOND_SWORD.get());
                                output.accept(ItemRegistry.WOODEN_HEAVY_HAMMER.get());
                                output.accept(ItemRegistry.STONE_HEAVY_HAMMER.get());
                                output.accept(ItemRegistry.IRON_HEAVY_HAMMER.get());
                                output.accept(ItemRegistry.WITHERED_GLIMMER_BLADE.get());


                                //机械
                                output.accept(BlockRegistry.FO_BENCH.get());
                                output.accept(BlockRegistry.LOWER_LEVER_CRUSHER.get());

                                //材料
                                output.accept(ItemRegistry.IRON_DUST.get());
                                output.accept(ItemRegistry.LUMINESCENT_DUST.get());

                                //花
                                output.accept(FlowerRegistry.LUMINEX_FLOWER.get());
                                output.accept(FlowerRegistry.DEEP_ORCHID.get());

                                //方块
                                output.accept(BlockRegistry.FLUORITE_ORE_0.get());
                                output.accept(BlockRegistry.FLUORITE_ORE_1.get());
                                output.accept(BlockRegistry.FLUORITE_ORE.get());
                                output.accept(BlockRegistry.DEEP_GLOW_STONE.get());
                                output.accept(BlockRegistry.GLOW_ALTAR.get());
                                output.accept(BlockRegistry.LIGHT_VEINED_STONE.get());
                                output.accept(BlockRegistry.CHISELED_GLOW_VEIN_QUARTZ.get());

                                //test
                                output.accept(BlockRegistry.GLOWTOMBSTONE.get());





                            })
                            .build()
            );
}
