package ii52.FoWorld.blockentity;


import ii52.FoWorld.menu.FoBenchMenu;
import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
import ii52.FoWorld.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
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

/**
 * FoBenchBlockEntity (方块实体)
 * 作用：它是方块的“灵魂”。普通方块只是一张贴图，而实体能存数据、能思考。
 */
public class FoBenchBlockEntity extends BlockEntity implements MenuProvider {

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

        // 创建一个临时的合成容器，模拟原版工作台的九宫格
        // 这个容器是虚拟的，它的唯一作用是作为参数传给配方系统进行“匹配检测”
        TransientCraftingContainer craftInv = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
            @Override
            public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
            @Override
            public boolean stillValid(Player player) { return true; }
        }, 3, 3);

        // 将我们 itemHandler 里的 0-8 号格子的物品“投影”到虚拟容器中
        for (int i = 0; i < 9; i++) {
            craftInv.setItem(i, itemHandler.getStackInSlot(i));
        }

        // 【核心步骤】向配方管理器询问：
        // “在我的 FO_BENCH_TYPE 分类下，有没有哪个配方能匹配这 9 个格子？”
        // 这里拿到的 recipe 对象，本质上就是你写的 FoBenchShapedRecipe 实例
        Optional<? extends CraftingRecipe> recipe = this.level.getRecipeManager()
                .getRecipeFor(RecipeRegistry.FO_BENCH_TYPE.get(), craftInv, this.level);

        // 获取当前输出槽（第 10 格，索引为 9）的内容
        ItemStack currentOutput = itemHandler.getStackInSlot(9);

        if (recipe.isPresent()) {
            // 【多态调用】本质上是调用了 FoBenchShapedRecipe 类里你写的 assemble 方法
            // 它会通过内部的 internal(原版ShapedRecipe) 计算出结果 ItemStack
            ItemStack resultStack = recipe.get().assemble(craftInv, this.level.registryAccess());

            // 为了防止逻辑死循环（setStackInSlot 会再次触发 onContentsChanged），
            // 只有当计算出的产物与当前输出槽的东西不一致时，才更新输出槽
            if (!ItemStack.matches(currentOutput, resultStack)) {
                itemHandler.setStackInSlot(9, resultStack);
            }
        } else {
            // 如果没有任何配方匹配，且输出槽不是空的，就清空输出槽
            if (!currentOutput.isEmpty()) {
                itemHandler.setStackInSlot(9, ItemStack.EMPTY);
            }
        }
    }

    // --- 2. 对外窗口 (The Capability) ---
    // 这是 Forge 的核心机制。别人（比如漏斗、管道、Menu）想拿你里面的东西，不能直接抢，
    // 必须通过这个 lazyItemHandler “礼貌地询问”。
    // LazyOptional 相当于一个包装盒，还没开机时是空的。
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public FoBenchBlockEntity(BlockPos pos, BlockState state){
        // 告诉父类：我是谁（注册名）、我在哪（坐标）、我长啥样（状态）
        super(BlockEntityRegistry.FO_BENCH_BE.get(), pos, state);
    }

    // 设置玩家在 UI 顶部看到的文字名称
    @Override
    public Component getDisplayName() {
        return Component.literal("世界工作台");
    }

    // --- 3. 连接逻辑 (The Connection) ---
    // 当你右键方块，服务器准备好打开 UI 时，会调用这个方法创建“管道”（Menu）
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        // 我们把 this (也就是这个实体自己) 传给 Menu。
        // 这样 Menu 就能通过 getCapability 摸到上面的 itemHandler 了。
        return new FoBenchMenu(id, playerInv, ContainerLevelAccess.create(this.level, this.worldPosition),itemHandler);
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