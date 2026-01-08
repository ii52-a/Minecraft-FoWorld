package ii52.FoWorld.blockentity;

import ii52.FoWorld.menu.LowerLeverCrusherMenu;
import ii52.FoWorld.recipe.CrushingRecipe;
import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
import ii52.FoWorld.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * 初级粉碎机方块实体
 * 逻辑流：tick(检查配方 -> 涨进度) -> 进度满(执行配方) -> 更新UI
 */
public class LowerLeverCrusherEntity extends BlockEntity implements MenuProvider {

    // ContainerData 是连接“后端数据”与“前端UI”的桥梁
    // 这里的数据会通过网络包自动同步给打开 GUI 的玩家
    protected final ContainerData data;
    private int progress = 0;           // 当前处理进度
    private final int maxProgress = 100; // 总计需要的时间 (20tick = 1秒)

    public LowerLeverCrusherEntity(BlockPos pos, BlockState state){
        super(BlockEntityRegistry.LOWER_LEVER_CRUSHER.get(), pos, state);

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                // 当 UI 询问“0号数据”时，返回当前进度
                return index == 0 ? LowerLeverCrusherEntity.this.progress : 0;
            }

            @Override
            public void set(int index, int value) {
                // 当 UI 试图修改数据时（虽然通常是只读），同步到变量
                if (index == 0) LowerLeverCrusherEntity.this.progress = value;
            }

            @Override
            public int getCount() { return 1; } // 总共只有一个变量需要同步
        };
    }

    // --- 1. 物品处理器 (Internal Inventory) ---
    // 设置为 3 个槽位：0-输入，1-主产物，2-副产物
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            // 只要箱子里的东西变了（玩家放了东西或机器吐了东西），就标记方块需要保存到磁盘
            setChanged();
        }
    };

    /**
     * 每 tick 执行一次的逻辑（由 Block 类调用）
     * 只有在服务端运行（!level.isClientSide）
     */
    public static void tick(Level level, BlockPos pos, BlockState state, LowerLeverCrusherEntity pEntity) {
        if (level.isClientSide) return;

        // 第一步：询问“我现在能开工吗？”
        if (pEntity.canCrush()) {
            pEntity.progress++; // 进度自增
            setChanged(level, pos, state); // 标记状态改变，确保进度被记录

            // 第二步：如果进度条满了
            if (pEntity.progress >= pEntity.maxProgress) {
                pEntity.executeCrush(); // 真正消耗原料并产出
                pEntity.progress = 0;   // 重置进度
            }
        } else {
            // 如果配方不匹配（比如把原料拿走了），进度立刻清零
            pEntity.progress = 0;
        }
    }

    /**
     * 核心逻辑：配方匹配检查
     */
    private boolean canCrush() {
        // 1. 获取当前输入槽的物品
        ItemStack input = itemHandler.getStackInSlot(0);
        if(input.isEmpty()) return false;

        // 2. 将我们的 itemHandler 包装成 SimpleContainer
        // 因为 Vanilla 的 RecipeManager 只认识 Container 接口，不认识 Forge 的 IItemHandler
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }
        // 3. 查表：从注册的 foworld:crushing 类型中寻找匹配当前容器内物品的配方
        Optional<CrushingRecipe> recipe = this.level.getRecipeManager()
                .getRecipeFor(RecipeRegistry.CRUSHING_TYPE.get(), container, this.level);

        // 如果找不到对应配方，直接收工
//        System.out.println(recipe.isEmpty());
        if (recipe.isEmpty()) return false;



        // 4. 预测产物：拿到配方里写好的产物信息
        CrushingRecipe crushingRecipe = recipe.get();
        ItemStack result = crushingRecipe.getResultItem(this.level.registryAccess());
        // 5. 空间检查：主产物槽位放得下吗？副产物槽位放得下吗？
        // 如果槽位里已经有东西了，必须是同种物品且没到堆叠上限（64）
        return canInsertIntoOutput(1, result.getItem());
    }

    /**
     * 核心逻辑：执行粉碎动作
     */
    private void executeCrush() {
        // 再次包装容器用于最后比对
        SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            container.setItem(i, itemHandler.getStackInSlot(i));
        }

        // 获取配方对象
        this.level.getRecipeManager()
                .getRecipeFor(RecipeRegistry.CRUSHING_TYPE.get(), container, this.level)
                .ifPresent(crushingRecipe -> {

                    // 1. 扣除消耗：从 0 号槽位取走 1 个物品
                    // false 表示真实操作，不是模拟
                    this.itemHandler.extractItem(0, 1, false);

                    // 2. 注入主产物：放入 1 号槽位
                    ItemStack result = crushingRecipe.getResultItem(this.level.registryAccess());
                    this.itemHandler.insertItem(1, result.copy(), false);

                    // 3. 判定副产物：生成一个随机数(0.0-1.0)与配方里的几率比对
                    for (CrushingRecipe.SecondaryResult sr : crushingRecipe.secondaryResults) {
                        // 每个副产物独立判定概率
                        if (this.level.random.nextFloat() < sr.chance()) {
                            ItemStack secStack = sr.stack();
                            // 尝试插入 2 号槽位
                            // insertItem 会自动处理“如果槽位满了就剩余”的逻辑
                            this.itemHandler.insertItem(2, secStack.copy(), false);
                            break;
                        }
                    }
                });
    }

    /**
     * 辅助工具：检查指定槽位是否可以塞入特定物品
     */
    private boolean canInsertIntoOutput(int slot, net.minecraft.world.item.Item item) {
        ItemStack stack = itemHandler.getStackInSlot(slot);
        // 情况 A：槽位是空的 -> 可以塞
        // 情况 B：物品相同且没满 -> 可以塞
        return stack.isEmpty() || (stack.is(item) && stack.getCount() < stack.getMaxStackSize());
    }

    // --- 2. 接口与系统集成 ---

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    @Override
    public Component getDisplayName() {
        return Component.literal("石制粉碎机");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        // 创建 Menu 时，把 itemHandler 和 data 塞进去，这样 UI 才能看到进度和物品
        return new LowerLeverCrusherMenu(id, playerInv,
                ContainerLevelAccess.create(this.level, this.worldPosition), itemHandler, this.data);
    }
    private final LazyOptional<IItemHandler> inputLazy = LazyOptional.of(() -> new RangedWrapper(itemHandler, 0, 1));
    private final LazyOptional<IItemHandler> outputLazy = LazyOptional.of(() -> new RangedWrapper(itemHandler, 1, 3));
    private final LazyOptional<IItemHandler> combinedLazy = LazyOptional.of(() -> itemHandler);
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
//         告诉漏斗或者其它机器：“我有存东西的能力”
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            // 如果 side 为 null，通常意味着不是从某个面访问，而是系统整体访问（比如破坏方块时）
            // 这时必须返回完整的、无限制的 handler，否则破坏方块不掉落
            if (side == null) {
                return combinedLazy.cast();
            }

            // 下方漏斗访问：返回输出视图
            if (side == Direction.DOWN) {
                return outputLazy.cast();
            }

            // 其他任何方向（上、东南西北）：返回输入视图
            return inputLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // 初始化能力
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        // 销毁能力，释放内存
        super.invalidateCaps();
        inputLazy.invalidate();
        outputLazy.invalidate();
        combinedLazy.invalidate();
    }

    // --- 3. 存档系统 (NBT) ---

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        // 读取物品
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        // 读取进度（非常重要，否则重启游戏进度清零）
        progress = nbt.getInt("progress");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        // 保存物品
        nbt.put("inventory", itemHandler.serializeNBT());
        // 保存当前进度
        nbt.putInt("progress", progress);
        super.saveAdditional(nbt);
    }
    public IItemHandler getItemHandler(){

        return this.itemHandler;
    }

}