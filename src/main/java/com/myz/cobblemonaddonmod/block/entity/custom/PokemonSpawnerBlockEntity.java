package com.myz.cobblemonaddonmod.block.entity.custom;

import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class PokemonSpawnerBlockEntity extends BlockEntity {

    private int tickCounter = 0;
    private static final int CHECK_INTERVAL = 10;
    private DataReceiverBlockEntity dataReceiverBlockEntity;
    private String pokemonOnBlock;
    private int gameMode;

    public PokemonSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POKEMON_SPAWN_EN, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, PokemonSpawnerBlockEntity blockEntity) {
        if (world.isClient) return;

        blockEntity.tickCounter++;
        if (blockEntity.tickCounter < CHECK_INTERVAL) return;
        blockEntity.tickCounter = 0;

        // Define the search area around the block
        Box searchBox = new Box(pos).expand(0.5, 1.5, 0.5);

        // Get all items in the search area
        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, searchBox, item -> true);

        for (ItemEntity item : items) {
            // Check if item is on top of the block
            if (item.getY() > pos.getY() && item.getY() < pos.getY() + 1.5) {

                // Calculate offset from block center
                double offsetX = item.getX() - (pos.getX() + 0.5);
                double offsetZ = item.getZ() - (pos.getZ() + 0.5);
                double distance = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);

                if (distance < 0.3) {
                    // If item is close to center, nudge in a random direction
                    offsetX = (Math.random() - 0.5) * 2;
                    offsetZ = (Math.random() - 0.5) * 2;
                    distance = Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
                }

                // Normalize and apply velocity
                double speed = 0.04; // Adjust this for nudge strength
                if (distance > 0) {
                    item.setVelocity(
                            (offsetX / distance) * speed,
                            0.1,
                            (offsetZ / distance) * speed
                    );
                }
            }
        }
    }

    public DataReceiverBlockEntity getDataReceiverBlockEntity() {
        return dataReceiverBlockEntity;
    }

    public void setDataReceiverBlockEntity(DataReceiverBlockEntity dataReceiverBlockEntity) {
        this.dataReceiverBlockEntity = dataReceiverBlockEntity;
    }

    public String getPokemonOnBlock() {
        return pokemonOnBlock;
    }

    public void setPokemonOnBlock(String pokemonOnBlock) {
        this.pokemonOnBlock = pokemonOnBlock;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        if(gameMode > 1)
            gameMode = 0;
        this.gameMode = gameMode;
    }
}