package com.myz.cobblemonaddonmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.block.entity.custom.GrillBlockEntity;
import com.myz.cobblemonaddonmod.block.entity.custom.PokemonSpawnerBlockEntity;
import com.myz.cobblemonaddonmod.item.custom.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PokemonSpawnerBlock extends BlockWithEntity implements BlockEntityProvider{

    public static final MapCodec<PokemonSpawnerBlock> CODEC = PokemonSpawnerBlock.createCodec(PokemonSpawnerBlock::new);


    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PokemonSpawnerBlockEntity(pos,state);
    }

    // Define the HORIZONTAL_FACING property
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public PokemonSpawnerBlock(Settings settings)
    {
        super(settings);
        // Set a default state for the block, including the facing direction
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
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GrillBlockEntity) // This part seems unrelated to a spawner block, but kept for context.
            {
                net.minecraft.util.ItemScatterer.spawn(world, pos, (GrillBlockEntity) blockEntity);
                world.updateComparators(pos,this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) { // Only execute on the server side
            int radius = 30;
            List<BlockPos> foundPositions = new ArrayList<>();
            // Get the facing direction from the block's current state
            Direction facing = state.get(FACING);

            for (int i = 1; i <= radius; i++) {
                BlockPos checkPos = pos.offset(facing, i);
                if (world.getBlockState(checkPos).getBlock() == Blocks.OAK_PLANKS) {
                    foundPositions.add(checkPos);
                }
            }
            if (world.getServer() != null) {
                world.getServer().getPlayerManager().broadcast(
                        Text.literal("Found " + foundPositions.size() + " Oak Planks in " + facing.asString() + " direction."),
                        false // false = chat, true = action bar
                );
            }
        }


        return ActionResult.SUCCESS; // Indicate that the use action was handled
    }
}