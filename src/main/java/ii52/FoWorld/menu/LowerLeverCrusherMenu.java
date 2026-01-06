package ii52.FoWorld.menu;

import ii52.FoWorld.registry.BlockRegistry;
import ii52.FoWorld.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class LowerLeverCrusherMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final ContainerLevelAccess access;
    private static final int INPUT_SLOT_COUNT = 1;  // 3x3 输入区格数
    private static final int BE_SLOT_COUNT = 3;    // 机器总共格数

    // 构造函数 A：客户端渲染专用。
    // 客户端不需要真实的 BlockEntity 数据，只需一个空的 ItemStackHandler 占位显示。
    public LowerLeverCrusherMenu(int id, Inventory inv, FriendlyByteBuf data) {
        this(id, inv, ContainerLevelAccess.create(inv.player.level(), data.readBlockPos()), new ItemStackHandler(BE_SLOT_COUNT),new SimpleContainerData(1));
    }

    // 构造函数 B：服务端逻辑专用。
    // handler 是从 BlockEntity 传过来的真实数据仓库。
    public LowerLeverCrusherMenu(int id, Inventory playerInv, ContainerLevelAccess access, IItemHandler handler,ContainerData data) {
        super(MenuRegistry.LOWER_LEVER_CRUSHER.get(), id);
        this.data=data;
        this.access = access;

        // 1. 注册 3x3 输入网格 (索引 0-8)
        this.addSlot(new SlotItemHandler(handler,  0, 34, 32));

        // 2. 注册输出槽 (索引 9)
        // 我们通过匿名内部类重写 Slot 的行为，实现“只许取不许放”和“扣除材料”
        this.addSlot(new SlotItemHandler(handler, 1, 108, 32) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // 逻辑：防止玩家把物品手动塞进输出槽
            }
        });
        this.addSlot(new SlotItemHandler(handler, 2, 138, 32) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false; // 逻辑：防止玩家把物品手动塞进输出槽
            }

        });

        // 3. 注册玩家背包 (索引 10-45)
        layoutPlayerInventory(playerInv, 8, 84);
        this.addDataSlots(data);
    }


    /**
     * 快速移动（Shift + 点击）逻辑：
     * 这是 Menu 中最复杂的部分，必须处理好物品在“玩家背包”与“机器槽位”之间的双向搬运。
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // 情况一：点击的是【输出槽】 (Index 9)'
            if (index==1 || index==2) {
                if (!this.moveItemStackTo(itemstack1, BE_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);

            }
            // 情况二：点击的是【机器输入槽】 (Index 0-8)
            else if (index ==0) {
                // 尝试搬到玩家背包
                if (!this.moveItemStackTo(itemstack1, BE_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // 情况三：点击的是【玩家背包】 (Index 10-45)
            else {
                // 尝试搬进机器的【输入槽】 (Index 0-9)
                // 注意：由于输出槽 (9) 的 mayPlace 返回 false，物品会自动跳过它进入 0-8
                if (!this.moveItemStackTo(itemstack1, 0, INPUT_SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // 数据清理逻辑
            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY); // 如果物品搬空了，彻底清空槽位
            } else {
                slot.setChanged(); // 否则告诉客户端：数量变了
            }

            // 如果搬运了一圈发现数量压根没变，说明背包满了或者没地方塞，直接退出
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            // 最终收尾工作，处理拿走后的回调
            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        // 安全距离检查：如果玩家跑太远，自动关闭 UI
        return stillValid(access, player, BlockRegistry.LOWER_LEVER_CRUSHER.get());
    }

    private void layoutPlayerInventory(Inventory playerInv, int x, int y) {
        // 常规写法：绘制玩家的 3x9 仓库
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }
        // 常规写法：绘制玩家的 1x9 快捷栏
        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, x + k * 18, y + 58));
        }
    }
    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = 100; // 必须和 BE 里的 maxProgress 一致
        int arrowSize = 24;    // 你在 PS 里量的箭头像素宽度
        return progress != 0 ? progress * arrowSize / maxProgress : 0;
    }
}