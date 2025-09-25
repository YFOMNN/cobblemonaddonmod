package com.myz.cobblemonaddonmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.PokemonSpawnHelper;
import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.custom.DataReceiverBlockEntity;
import com.myz.cobblemonaddonmod.block.entity.custom.HighestBstBlockEntity;
import com.myz.cobblemonaddonmod.block.entity.custom.PokemonSpawnerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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

public class HighestBstBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private boolean gameStarted = false;

    public static final MapCodec<HighestBstBlock> CODEC = HighestBstBlock.createCodec(HighestBstBlock::new);
    public static final BooleanProperty POWERED = Properties.POWERED;


    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HighestBstBlockEntity(pos,state);
    }

    public HighestBstBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // Register the FACING property with the block's state manager
        builder.add(FACING);
    }
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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

            HighestBstBlockEntity highestBstBlockEntity = (HighestBstBlockEntity) world.getBlockEntity(pos);
            player.openHandledScreen(highestBstBlockEntity);

        }
        return ActionResult.SUCCESS;
    }
}
