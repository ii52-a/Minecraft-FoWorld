package ii52.FoWorld.blockentity;

import ii52.FoWorld.menu.FoBenchMenu;
import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
import ii52.FoWorld.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Optional;

public class LightDeciphererEntity extends BlockEntity {
    // --- 1. 物品仓库 (The Storage) ---
    // ItemStackHandler 是 Forge 提供的“超级箱子”工具。
    // 这里参数设为 1，代表这个方块内部只有一个格子。
    private final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            // 【重要】每当格子里的东西变多、变少或换了种类，都要喊一声：“我变了！”
            // 这样游戏才知道该把这个方块的数据写进硬盘存档了。
            setChanged();
            updateChanged();
        }
    };
    // --- 1.5 处理结果
    // --- 1.5 核心合成处理逻辑 ---
    private void updateChanged() {
        // 只有在服务端才进行合成逻辑计算（客户端只负责显示，不负责逻辑校验）
        if (this.level == null || this.level.isClientSide) return;


    }

    // --- 2. 对外窗口 (The Capability) ---
    // 这是 Forge 的核心机制。别人（比如漏斗、管道、Menu）想拿你里面的东西，不能直接抢，
    // 必须通过这个 lazyItemHandler “礼貌地询问”。
    // LazyOptional 相当于一个包装盒，还没开机时是空的。
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public LightDeciphererEntity(BlockPos pos, BlockState state){
        // 告诉父类：我是谁（注册名）、我在哪（坐标）、我长啥样（状态）
        super(BlockEntityRegistry.FO_BENCH_BE.get(), pos, state);
    }





    // --- 4. 能力授权 (The Permissions) ---
    // 这是一个固定写法。当别人问：“你有物品操作能力吗？”
    // 我们回答：“有（ForgeCapabilities.ITEM_HANDLER），就是我包里的那个 lazyItemHandler”。
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    // 方块加载进世界时，把包装盒塞进数据
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    // 方块被拆掉时，把包装盒扔掉，防止占用内存
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    // --- 5. 记忆功能 (NBT Save/Load) ---
    // 如果不写这两个方法，你存进格子的东西，重进游戏就没了。

    // 从存档文件读取数据（游戏启动、区块加载时调用）
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        // 找到名为 "inventory" 的记事本页，把物品还原出来
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    // 把数据写入存档文件（玩家下线、保存游戏时调用）
    @Override
    protected void saveAdditional(CompoundTag nbt) {
        // 把 itemHandler 里的物品打包成 NBT 贴上 "inventory" 标签
        nbt.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(nbt);
    }
}
