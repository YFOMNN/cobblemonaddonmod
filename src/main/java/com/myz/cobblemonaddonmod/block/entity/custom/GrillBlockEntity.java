package com.myz.cobblemonaddonmod.block.entity.custom;

import com.myz.cobblemonaddonmod.block.entity.ImplementedInventory;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class GrillBlockEntity extends BlockEntity implements ImplementedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1,ItemStack.EMPTY);

    public GrillBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRILL_RE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty(); // Mark dirty for NBT saving
        if (this.world != null && !this.world.isClient()) {
            // Send update packet to clients immediately
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(this.inventory, slot, amount);
        if (!result.isEmpty()) {
            markDirty(); // Mark dirty for NBT saving
            if (this.world != null && !this.world.isClient()) {
                // Send update packet to clients immediately
                this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
            }
        }
        return result;
    }

    @Override
    public void clear() {
        this.inventory.clear(); // This sets all slots to ItemStack.EMPTY
        markDirty(); // Mark dirty for NBT saving
        if (this.world != null && !this.world.isClient()) {
            // Send update packet to clients immediately
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        }
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        // This method is called by updateListeners to get the NBT data for the packet.
        // It's crucial that createNbt() accurately reflects the current state.
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        // This is called when a chunk is first sent to the client.
        // It should also provide the correct initial state.
        return createNbt(registryLookup);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        // Ensure that even if the inventory is empty, the NBT accurately reflects it.
        // Inventories.writeNbt should handle ItemStack.EMPTY correctly.
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        // Ensure the inventory is cleared before reading, just in case.
        // Then read the NBT data into the inventory.
        this.inventory.clear(); // Clear existing items
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }
}