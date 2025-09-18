package com.myz.cobblemonaddonmod.block.entity.custom;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
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
import java.util.Random;

public class DataReceiverBlockEntity extends BlockEntity {
    public  List<BlockPos> spawnPositions = new ArrayList<>();
    public DataReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DATA_RECEIVER_EN, pos, state);
    }

    public void getSideSpawnPoints(BlockState state, World world, BlockPos pos, PlayerEntity player)
    {
        if (!world.isClient()) {
            spawnPositions.clear();
            if (world instanceof ServerWorld serverWorld)
                PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, this.getPos());
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
                        scanner.setPokemonOnBlock(null);
                        scanner.setDataReceiverBlockEntity(this);
                        scanner.setGameMode(0);
                    }
                }
            }
            clearSpawnPoints();
            player.sendMessage(
                    Text.literal("Receiver collected " + spawnPositions.size() + " Oak Planks from nearby scanners."),
                    false
            );
        }
    }
    public void clearSpawnPoints()
    {
        if (!world.isClient()) {
            for(BlockPos spawnPos: spawnPositions)
            {
                BlockEntity be = world.getBlockEntity(spawnPos);
                if (be instanceof PokemonSpawnerBlockEntity scanner) {
                    scanner.setPokemonOnBlock(null);
                    scanner.setDataReceiverBlockEntity(null);
                    if (world instanceof ServerWorld serverWorld) {
                        PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, spawnPos);
                    }
                }
            }
        }
    }

    public void prepareForMemoryGame() {
        if (!world.isClient()) {
            int numberOfPairs = spawnPositions.size() / 2;
            List<int[]> pairs = new ArrayList<>();
            List<String> selectedPokemon = new ArrayList<>();
            List<Integer> availableNumbers = new ArrayList<>();

            for(int i= 0; i<numberOfPairs*2 ; i++)
                availableNumbers.add(i);
            int k;
            for (int i = 0;i<numberOfPairs;i++)
            {
                selectedPokemon.add(PokemonSpawnHelper.pickPokemon(false));
                Random random =  new Random();

                int pos1,pos2;
                k = random.nextInt(availableNumbers.size());
                pos1 = availableNumbers.get(k);
                availableNumbers.remove(k);

                k = random.nextInt(availableNumbers.size());
                pos2 = availableNumbers.get(k);
                availableNumbers.remove(k);
                pairs.add(new int[]{pos2, pos1});

            }
            CobblemonAddonMod.LOGGER.info("Registering mod items for:"  + CobblemonAddonMod.MOD_ID);

            for(BlockPos spawnPos: spawnPositions)
            {
                BlockEntity be = world.getBlockEntity(spawnPos);
                if (be instanceof PokemonSpawnerBlockEntity scanner) {
                    scanner.setGameMode(1);
                }
            }
        }
    }
}
