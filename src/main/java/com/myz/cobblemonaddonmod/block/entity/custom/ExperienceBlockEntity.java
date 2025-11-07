package com.myz.cobblemonaddonmod.block.entity.custom;

import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class ExperienceBlockEntity extends BlockEntity {

    private long lastUsedTime = 0;

    public ExperienceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXPERIENCE_BLOCK_EN, pos, state);
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }

    public void setLastUsedTime(long time) {
        this.lastUsedTime = time;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putLong("LastUsedTime", lastUsedTime);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        lastUsedTime = nbt.getLong("LastUsedTime");
    }
}