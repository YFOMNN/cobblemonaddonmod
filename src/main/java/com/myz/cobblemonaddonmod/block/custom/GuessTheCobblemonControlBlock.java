package com.myz.cobblemonaddonmod.block.custom;

import com.myz.cobblemonaddonmod.PokemonSpawnHelper;
import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.custom.DataReceiverBlockEntity;
import com.myz.cobblemonaddonmod.block.entity.custom.PokemonSpawnerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GuessTheCobblemonControlBlock extends Block {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;


    public GuessTheCobblemonControlBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // Register the FACING property with the block's state manager
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        // Set the block's facing direction based on the player's look direction when placed
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            int radius = 10; // how far around to check
            List<BlockPos> foundPositions = new ArrayList<>();

            for (BlockPos checkPos : BlockPos.iterate(pos.add(-radius, -radius, -radius),
                    pos.add(radius, radius, radius))) {
                if (world.getBlockState(checkPos).getBlock() == ModBlocks.DATA_RECEIVER) {
                    foundPositions.add(checkPos.toImmutable());
               }
            }

            int numberOfPokemonPerSide = 100;

            if(foundPositions.size() > 1)
            {
                for(BlockPos bp: foundPositions)
                {
                    BlockEntity be = world.getBlockEntity(bp);
                    if (be instanceof DataReceiverBlockEntity scanner) {
                        numberOfPokemonPerSide = Math.min(numberOfPokemonPerSide, scanner.spawnPositions.size());
                    }
                }
                List<String> pokemon = new ArrayList<>();
                for (int i = 0; i< numberOfPokemonPerSide;i++)
                {
                    pokemon.add(PokemonSpawnHelper.pickPokemon(true));
                }
                for(BlockPos bp: foundPositions)
                {
                    BlockEntity be = world.getBlockEntity(bp);
                    if (be instanceof DataReceiverBlockEntity scanner) {
                        scanner.clearSpawnPoints();
                        for(int j = 0; j < scanner.spawnPositions.size(); j++)
                        {
                            BlockEntity spawnloc = world.getBlockEntity(scanner.spawnPositions.get(j));
                            if(spawnloc instanceof PokemonSpawnerBlockEntity pokemonSpawnerBlockEntity){
                                pokemonSpawnerBlockEntity.setPokemonOnBlock(pokemon.get(j));
                                pokemonSpawnerBlockEntity.setDataReceiverBlockEntity(scanner);
                                PokemonSpawnHelper.spawnPokemonAt(Objects.requireNonNull(world), scanner.spawnPositions.get(j), pokemon.get(j), "uncatchable");
                            }
                        }
                    }
                }
            }

        }
        return ActionResult.SUCCESS;
    }
}
