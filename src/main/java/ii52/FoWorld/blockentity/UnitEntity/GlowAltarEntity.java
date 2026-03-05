package ii52.FoWorld.blockentity.UnitEntity;


import ii52.FoWorld.recipe.GlowAltarRecipe;
import ii52.FoWorld.registry.BlockRegistry.BlockEntityRegistry;
import ii52.FoWorld.registry.BlockRegistry.BlockRegistry;
import ii52.FoWorld.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GlowAltarEntity extends BlockEntity {

    private List<BlockPos> LinkListPos=List.of(
            new BlockPos(-2, 0, -2), new BlockPos(-2, 0, 2),
            new BlockPos(2, 0, -2),  new BlockPos(2, 0, 2),
            new BlockPos(0, 0, 3), new BlockPos(0, 0, -3),
            new BlockPos(3, 0, 0),  new BlockPos(-3, 0, 0)
    );
    private boolean is_active=false;
    private int link_place=0;
    private List<Boolean> link_list =new ArrayList<>(Collections.nCopies(8, false));
    
    private int processingTime = 0;
    private int totalProcessingTime = 0;
    private ItemStack resultItem = ItemStack.EMPTY;
    
    public GlowAltarEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.GLOW_ALTAR.get(), pos, state);
    }
    
    public boolean getActiveStatus(){
        return this.is_active;
    }

    public boolean change_base(Level level){
        if (!level.isClientSide()){
            for (int index = 0; index < LinkListPos.size(); index++){
                if (!link_list.get(index)){
                    BlockPos target = this.worldPosition.offset(LinkListPos.get(index));
                    if (level.getBlockState(target).is(Blocks.STONE)){
                        level.setBlockAndUpdate(target, BlockRegistry.LIGHT_VEINED_STONE.get().defaultBlockState());
                        BlockEntity subBE = level.getBlockEntity(target);
                        if (subBE instanceof LightVeinedStoneEntity child) {
                            child.setCorePos(this.worldPosition);
                        }
                        link_list.set(index, true);
                        this.link_place++;
                        level.levelEvent(2001, target, net.minecraft.world.level.block.Block.getId(Blocks.STONE.defaultBlockState()));
                        check_active();
                        this.setChanged();
                        return true;
                    }
                }
            }
            check_active();
            return false;
        }
        return false;
    }

    public boolean distributeItemToPedestal(Level level, ItemStack itemStack) {
        if (!level.isClientSide && is_active && !itemStack.isEmpty()) {
            for (int index = 0; index < LinkListPos.size(); index++) {
                if (link_list.get(index)) {
                    BlockPos targetPos = this.worldPosition.offset(LinkListPos.get(index));
                    BlockEntity be = level.getBlockEntity(targetPos);
                    if (be instanceof LightVeinedStoneEntity stoneEntity) {
                        if (!stoneEntity.hasItem() && isPedestalAvailable(level, targetPos)) {
                            stoneEntity.setStoredItem(itemStack.split(1));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isPedestalAvailable(Level level, BlockPos pedestalPos) {
        BlockPos abovePos = pedestalPos.above();
        return level.getBlockState(abovePos).isAir();
    }

    public List<LightVeinedStoneEntity> getPedestalsWithItems(Level level) {
        List<LightVeinedStoneEntity> pedestals = new ArrayList<>();
        for (int index = 0; index < LinkListPos.size(); index++) {
            if (link_list.get(index)) {
                BlockPos targetPos = this.worldPosition.offset(LinkListPos.get(index));
                BlockEntity be = level.getBlockEntity(targetPos);
                if (be instanceof LightVeinedStoneEntity stoneEntity) {
                    if (stoneEntity.hasItem()) {
                        pedestals.add(stoneEntity);
                    }
                }
            }
        }
        return pedestals;
    }

    public SimpleContainer createContainerFromPedestals(Level level) {
        List<LightVeinedStoneEntity> pedestals = getPedestalsWithItems(level);
        SimpleContainer container = new SimpleContainer(pedestals.size());
        for (int i = 0; i < pedestals.size(); i++) {
            container.setItem(i, pedestals.get(i).getStoredItem());
        }
        return container;
    }

    public Optional<GlowAltarRecipe> findMatchingRecipe(Level level) {
        if (!is_active) return Optional.empty();
        
        SimpleContainer container = createContainerFromPedestals(level);
        return level.getRecipeManager().getRecipeFor(RecipeRegistry.GLOW_ALTAR_TYPE.get(), container, level);
    }

    public void tick(Level level) {
        if (!level.isClientSide && is_active) {
            Optional<GlowAltarRecipe> recipeOpt = findMatchingRecipe(level);
            
            if (recipeOpt.isPresent()) {
                GlowAltarRecipe recipe = recipeOpt.get();
                
                if (totalProcessingTime == 0) {
                    totalProcessingTime = recipe.getProcessingTime();
                    resultItem = recipe.getResultItem(level.registryAccess()).copy();
                }
                
                processingTime++;
                
                if (processingTime >= totalProcessingTime) {
                    completeRecipe(level, recipe);
                    processingTime = 0;
                    totalProcessingTime = 0;
                    resultItem = ItemStack.EMPTY;
                }
                
                this.setChanged();
            } else {
                processingTime = 0;
                totalProcessingTime = 0;
                resultItem = ItemStack.EMPTY;
            }
        }
    }

    private void completeRecipe(Level level, GlowAltarRecipe recipe) {
        List<LightVeinedStoneEntity> pedestals = getPedestalsWithItems(level);
        List<Ingredient> ingredients = new ArrayList<>(recipe.getInputs());
        
        for (LightVeinedStoneEntity pedestal : pedestals) {
            ItemStack storedItem = pedestal.getStoredItem();
            for (int i = 0; i < ingredients.size(); i++) {
                if (ingredients.get(i).test(storedItem)) {
                    pedestal.dropItem(level);
                    ingredients.remove(i);
                    break;
                }
            }
        }
        
        for (int index = 0; index < LinkListPos.size(); index++) {
            if (link_list.get(index)) {
                BlockPos targetPos = this.worldPosition.offset(LinkListPos.get(index));
                BlockEntity be = level.getBlockEntity(targetPos);
                if (be instanceof LightVeinedStoneEntity stoneEntity) {
                    if (!stoneEntity.hasItem() && isPedestalAvailable(level, targetPos)) {
                        stoneEntity.setStoredItem(resultItem.copy());
                        break;
                    }
                }
            }
        }
    }

    public void onPartRemoved(BlockPos pos){
        BlockPos relative = pos.subtract(this.worldPosition);
        int index=this.LinkListPos.indexOf(relative);
        link_list.set(index,false);
        link_place--;
        check_active();
        this.setChanged();
    }
    
    public void check_active(){
        this.is_active= link_place == 8;
    }

    public void dropAllPedestalItems(Level level) {
        if (!level.isClientSide) {
            for (int index = 0; index < LinkListPos.size(); index++) {
                if (link_list.get(index)) {
                    BlockPos targetPos = this.worldPosition.offset(LinkListPos.get(index));
                    BlockEntity be = level.getBlockEntity(targetPos);
                    if (be instanceof LightVeinedStoneEntity stoneEntity) {
                        stoneEntity.onStructureBroken(level);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.putBoolean("is_active", this.is_active);
        nbt.putInt("link_place", this.link_place);
        nbt.putInt("processingTime", this.processingTime);
        nbt.putInt("totalProcessingTime", this.totalProcessingTime);
        if (!resultItem.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            resultItem.save(itemTag);
            nbt.put("resultItem", itemTag);
        }
        byte[] bools = new byte[link_list.size()];
        for (int i = 0; i < link_list.size(); i++) {
            bools[i] = (byte) (link_list.get(i) ? 1 : 0);
        }
        nbt.putByteArray("link_list", bools);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.is_active = nbt.getBoolean("is_active");
        this.link_place = nbt.getInt("link_place");
        this.processingTime = nbt.getInt("processingTime");
        this.totalProcessingTime = nbt.getInt("totalProcessingTime");
        if (nbt.contains("resultItem", 10)) {
            resultItem = ItemStack.of(nbt.getCompound("resultItem"));
        }
        if (nbt.contains("link_list")) {
            byte[] bools = nbt.getByteArray("link_list");
            for (int i = 0; i < bools.length && i < link_list.size(); i++) {
                link_list.set(i, bools[i] == 1);
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        saveAdditional(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public int getTotalProcessingTime() {
        return totalProcessingTime;
    }

    public float getProgress() {
        return totalProcessingTime > 0 ? (float) processingTime / totalProcessingTime : 0f;
    }
}
