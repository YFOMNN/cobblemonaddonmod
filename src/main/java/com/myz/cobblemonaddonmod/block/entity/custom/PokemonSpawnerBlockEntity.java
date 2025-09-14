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

    public SpawnManagerBlockEntity spawnManagerBlockEntity;
    public String pokemonOnBlock;

    public PokemonSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POKEMON_SPAWN_EN, pos, state);
    }

}
