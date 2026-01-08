package ii52.FoWorld.tier;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;


public class FoTier {

    // 建议为每种材质定义独立的 Tier，以便精准控制耐久度
    public static final Tier WOODEN_HAMMER = new ForgeTier(
            0,          // 采掘等级 (木级为 0)
            300,        // 自定义耐久度 (比普通木镐高)
            2.0f,       // 挖掘速度
            3.0f,       // 攻击伤害
            15,         // 附魔能力
            null,
            () -> Ingredient.of(Items.OAK_LOG)
    );

    public static final Tier STONE_HAMMER = new ForgeTier(
            1,          // 石级
            600,        // 耐久度
            4.0f,
            3.0f,
            5,
            null,
            () -> Ingredient.of(Items.COBBLESTONE)
    );

    // 依此类推...
}