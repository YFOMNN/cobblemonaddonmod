package com.myz.cobblemonaddonmod.block.entity.custom;

import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class PokemonSpawnerBlockEntity extends BlockEntity {

    private DataReceiverBlockEntity dataReceiverBlockEntity;
    private String pokemonOnBlock;
    private int gameMode;

    public PokemonSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POKEMON_SPAWN_EN, pos, state);
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
        if(gameMode>1)
            gameMode= 0;
        this.gameMode = gameMode;
    }
}
