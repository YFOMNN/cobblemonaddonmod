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
import java.util.Objects;
import java.util.Random;

public class DataReceiverBlockEntity extends BlockEntity {
    public  List<BlockPos> spawnPositions = new ArrayList<>();
    private PokemonSpawnerBlockEntity lastFlippedPokemonBlock;
    private boolean isPowered;

    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        isPowered = powered;
    }

    public DataReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DATA_RECEIVER_EN, pos, state);
    }
    public void updateFlippedPokemon(BlockEntity be)
    {
        CobblemonAddonMod.LOGGER.info("RUN  ");
        if (lastFlippedPokemonBlock == null)
        {
            lastFlippedPokemonBlock = (PokemonSpawnerBlockEntity) be ;
        }
        else
        {

            PokemonSpawnerBlockEntity pokemonSpawnerBlockEntity = (PokemonSpawnerBlockEntity) be;
            if(pokemonSpawnerBlockEntity.getPokemonOnBlock().equals(lastFlippedPokemonBlock.getPokemonOnBlock()))
            {
                CobblemonAddonMod.LOGGER.info("Correct");
            }
            else
            {
                CobblemonAddonMod.LOGGER.info("Wrong");
                if (world instanceof ServerWorld serverWorld) {
                    PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, lastFlippedPokemonBlock.getPos());
                    PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, be.getPos());
                }
            }
            lastFlippedPokemonBlock = null;
        }
    }
    public void getSideSpawnPoints(BlockState state, World world, BlockPos pos, PlayerEntity player)
    {
        if (!world.isClient()) {
            clearSpawnPoints();
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

            player.sendMessage(
                    Text.literal("Receiver collected " + spawnPositions.size() + " nearby scanners."),
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
            String selectedPokemon;
            for (int i = 0;i<numberOfPairs;i++)
            {
                selectedPokemon = (PokemonSpawnHelper.pickPokemon(false));
                Random random =  new Random();
                while(true)
                {
                    BlockEntity be = world.getBlockEntity(spawnPositions.get(random.nextInt(spawnPositions.size())));
                    if (be instanceof PokemonSpawnerBlockEntity scanner)
                    {
                        if(scanner.getPokemonOnBlock() == null)
                        {
                            scanner.setPokemonOnBlock(selectedPokemon);
                            CobblemonAddonMod.LOGGER.info("Set pokemon " + scanner.getPokemonOnBlock());


                            break;
                        }
                    }
                }
                while(true)
                {
                    BlockEntity be = world.getBlockEntity(spawnPositions.get(random.nextInt(spawnPositions.size())));
                    if (be instanceof PokemonSpawnerBlockEntity scanner)
                    {
                        if(scanner.getPokemonOnBlock() == null)
                        {
                            scanner.setPokemonOnBlock(selectedPokemon);
                            CobblemonAddonMod.LOGGER.info("Set pokemon " + scanner.getPokemonOnBlock());
                            break;
                        }
                    }
                }


            }

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
