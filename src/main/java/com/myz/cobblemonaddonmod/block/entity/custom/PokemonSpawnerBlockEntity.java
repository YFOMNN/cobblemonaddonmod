package com.myz.cobblemonaddonmod.block.entity.custom;

import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static com.myz.cobblemonaddonmod.block.custom.PokemonSpawnerBlock.FACING;

public class PokemonSpawnerBlockEntity extends BlockEntity {
    private final List<BlockPos> foundPositions = new ArrayList<>();

    public PokemonSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POKEMON_SPAWN_EN, pos, state);
    }

    // Scan forward and update results
    public void scan(World world, BlockState state, BlockPos pos) {
        foundPositions.clear();
        Direction facing = state.get(Properties.HORIZONTAL_FACING);

        for (int i = 1; i <= 30; i++) {
            BlockPos checkPos = pos.offset(facing, i);
            if (world.getBlockState(checkPos).getBlock() == Blocks.OAK_PLANKS) {
                foundPositions.add(checkPos);
            }
        }
    }

    public List<BlockPos> getFoundPositions() {
        return new ArrayList<>(foundPositions);
    }
}
