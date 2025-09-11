package com.myz.cobblemonaddonmod.block.custom;

import com.myz.cobblemonaddonmod.block.entity.custom.PokemonSpawnerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class DataReceiverBlock extends Block {

    public DataReceiverBlock(Settings settings) {
        super(settings);
    }
/*
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()) {
            Direction facing = getFacingSafe(world, pos, state, (player instanceof ServerPlayerEntity) ? (ServerPlayerEntity) player : null);

            List<BlockPos> allResults = new ArrayList<>();

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
                        scanner.scan(world, world.getBlockState(checkPos), checkPos);
                        allResults.addAll(scanner.getFoundPositions());
                    }
                }
            }
            player.sendMessage(
                    Text.literal("Receiver collected " + allResults.size() + " Oak Planks from nearby scanners."),
                    false
            );
        }
        return ActionResult.SUCCESS;
    }
    public static Direction getFacingSafe(World world, BlockPos pos, BlockState state, ServerPlayerEntity debugPlayer) {
        // if state is null, try to fetch it
        if (state == null && world != null && pos != null) state = world.getBlockState(pos);

        if (state == null) {
            if (debugPlayer != null) debugPlayer.sendMessage(Text.literal("No block state available — defaulting NORTH"), false);
            return Direction.NORTH;
        }

        // prefer HORIZONTAL_FACING if present
        if (state.contains(Properties.HORIZONTAL_FACING)) {
            return state.get(Properties.HORIZONTAL_FACING);
        }

        // fallback to the general FACING property (some blocks use this)
        if (state.contains(Properties.FACING)) {
            return state.get(Properties.FACING);
        }

        // fallback to HorizontalFacingBlock.FACING (if your block defines its own FACING)
        if (state.contains(HorizontalFacingBlock.FACING)) {
            return state.get(HorizontalFacingBlock.FACING);
        }

        // nothing found — warn once to the player or log and default north
        if (debugPlayer != null) debugPlayer.sendMessage(Text.literal("Block has no facing property; defaulting to NORTH"), false);
        return Direction.NORTH;
    }*/
}
