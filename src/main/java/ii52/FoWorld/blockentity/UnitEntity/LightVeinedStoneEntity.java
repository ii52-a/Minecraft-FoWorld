package ii52.FoWorld.blockentity.UnitEntity;

import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class LightVeinedStoneEntity extends BlockEntity{

    private BlockPos corePos;
    private ItemStack storedItem = ItemStack.EMPTY;
    private UUID displayItemUUID = null;
    private ItemEntity displayItemEntity = null;

    public LightVeinedStoneEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.LIGHT_VEINED_STONE.get(), pos, state);
    }

    public boolean hasItem() {
        return !storedItem.isEmpty();
    }

    public ItemStack getStoredItem() {
        return storedItem;
    }

    public void setStoredItem(ItemStack stack) {
        this.storedItem = stack.copy();
        this.setChanged();
        if (level != null && !level.isClientSide) {
            spawnDisplayItem();
        }
    }

    private void spawnDisplayItem() {
        if (level == null || level.isClientSide || storedItem.isEmpty()) return;
        
        removeDisplayItem();
        
        double x = worldPosition.getX() + 0.5;
        double y = worldPosition.getY() + 1.1;
        double z = worldPosition.getZ() + 0.5;
        
        displayItemEntity = new ItemEntity(level, x, y, z, storedItem.copy());
        displayItemEntity.setNoGravity(true);
        displayItemEntity.setPickUpDelay(Integer.MAX_VALUE);
        displayItemEntity.setUnlimitedLifetime();
        displayItemEntity.setDeltaMovement(Vec3.ZERO);
        displayItemEntity.setInvulnerable(true);
        displayItemUUID = displayItemEntity.getUUID();
        
        level.addFreshEntity(displayItemEntity);
        this.setChanged();
    }

    private void removeDisplayItem() {
        if (level == null || level.isClientSide) return;
        
        if (displayItemEntity != null && displayItemEntity.isAlive()) {
            displayItemEntity.discard();
        } else if (displayItemUUID != null) {
            Entity entity = ((net.minecraft.server.level.ServerLevel) level).getEntity(displayItemUUID);
            if (entity instanceof ItemEntity itemEntity) {
                itemEntity.discard();
            }
        }
        displayItemEntity = null;
        displayItemUUID = null;
    }

    public void dropItem(Level level) {
        if (!level.isClientSide && !storedItem.isEmpty()) {
            removeDisplayItem();
            
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.0;
            double z = worldPosition.getZ() + 0.5;
            
            ItemEntity itemEntity = new ItemEntity(level, x, y, z, storedItem.copy());
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
            
            storedItem = ItemStack.EMPTY;
            this.setChanged();
        }
    }

    public void tick(Level level) {
        if (!level.isClientSide && hasItem()) {
            if (displayItemEntity == null || !displayItemEntity.isAlive()) {
                if (displayItemUUID != null) {
                    Entity entity = ((net.minecraft.server.level.ServerLevel) level).getEntity(displayItemUUID);
                    if (entity instanceof ItemEntity itemEntity && itemEntity.isAlive()) {
                        displayItemEntity = itemEntity;
                    } else {
                        displayItemUUID = null;
                        spawnDisplayItem();
                    }
                } else {
                    spawnDisplayItem();
                }
            }
            
            if (displayItemEntity != null && displayItemEntity.isAlive()) {
                double targetX = worldPosition.getX() + 0.5;
                double targetY = worldPosition.getY() + 1.1;
                double targetZ = worldPosition.getZ() + 0.5;
                
                double dx = displayItemEntity.getX() - targetX;
                double dy = displayItemEntity.getY() - targetY;
                double dz = displayItemEntity.getZ() - targetZ;
                
                if (dx * dx + dy * dy + dz * dz > 0.01) {
                    displayItemEntity.setPos(targetX, targetY, targetZ);
                    displayItemEntity.setDeltaMovement(Vec3.ZERO);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if (this.corePos != null) {
            nbt.put("CorePos", NbtUtils.writeBlockPos(this.corePos));
        }
        if (!storedItem.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            storedItem.save(itemTag);
            nbt.put("StoredItem", itemTag);
        }
        if (displayItemUUID != null) {
            nbt.putUUID("DisplayItemUUID", displayItemUUID);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains("CorePos", 10)) {
            this.corePos = NbtUtils.readBlockPos(nbt.getCompound("CorePos"));
        }
        if (nbt.contains("StoredItem", 10)) {
            storedItem = ItemStack.of(nbt.getCompound("StoredItem"));
        }
        if (nbt.contains("DisplayItemUUID")) {
            displayItemUUID = nbt.getUUID("DisplayItemUUID");
        }
    }

    public void setCorePos(BlockPos pos) {
        this.corePos = pos;
        this.setChanged();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    public void notifyCore(Level level, BlockPos WorldPos) {
        if (this.corePos == null || level == null) return;
        if (!level.isLoaded(this.corePos)) return;
        if (corePos != null) {
            BlockEntity be = level.getBlockEntity(corePos);
            if (be instanceof GlowAltarEntity core) {
                core.onPartRemoved(WorldPos);
            }
        }
    }

    public void onStructureBroken(Level level) {
        dropItem(level);
    }

    public void clearDisplayItem() {
        removeDisplayItem();
    }
}
