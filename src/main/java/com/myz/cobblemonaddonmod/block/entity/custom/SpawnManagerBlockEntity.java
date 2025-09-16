package com.myz.cobblemonaddonmod.block.entity.custom;

import com.myz.cobblemonaddonmod.PokemonSpawnHelper;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpawnManagerBlockEntity extends BlockEntity {
    public  List<BlockPos> spawnPositions = new ArrayList<>();
    public SpawnManagerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DATA_RECEIVER_EN, pos, state);
    }

    public void getSideSpawnPoints(BlockState state, World world, BlockPos pos, PlayerEntity player)
    {
        if (!world.isClient()) {
            spawnPositions.clear();
            Direction facing = state.get(Properties.HORIZONTAL_FACING);

// Define length & width
            int length = 12; // forward
            int halfWidth = 6; // left/right from center

            for (int forward = 1; forward <= length; forward++) {
                for (int side = -halfWidth; side <= halfWidth; side++) {
                    BlockPos checkPos;

                    if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                        // forward = Z axis, side = X axis
                        checkPos = pos.add(side, 0, facing.getOffsetZ() * forward);
                    } else {
                        // forward = X axis, side = Z axis
                        checkPos = pos.add(facing.getOffsetX() * forward, 0, side);
                    }

                    BlockEntity be = world.getBlockEntity(checkPos);
                    if (be instanceof PokemonSpawnerBlockEntity scanner) {
                        spawnPositions.add(checkPos);
                        scanner.pokemonOnBlock = null;
                        if (world instanceof ServerWorld serverWorld) {
                            PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, checkPos);
                        }
                    }
                }
            }
            player.sendMessage(
                    Text.literal("Receiver collected " + spawnPositions.size() + " Oak Planks from nearby scanners."),
                    false
            );
        }
    }
}
