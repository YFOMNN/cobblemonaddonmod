package com.myz.cobblemonaddonmod.block.custom;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.PokemonSpawnHelper;
import com.myz.cobblemonaddonmod.block.entity.custom.GrillBlockEntity;
import com.myz.cobblemonaddonmod.block.entity.custom.PokemonSpawnerBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PokemonSpawnerBlock extends BlockWithEntity implements BlockEntityProvider{

    public static final MapCodec<PokemonSpawnerBlock> CODEC = PokemonSpawnerBlock.createCodec(PokemonSpawnerBlock::new);

    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");
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
        setDefaultState(getDefaultState().with(ACTIVE, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        // Register the FACING property with the block's state manager
        builder.add(FACING);
        builder.add(ACTIVE);

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
            if (blockEntity instanceof GrillBlockEntity)
            {
                net.minecraft.util.ItemScatterer.spawn(world, pos, (GrillBlockEntity) blockEntity);
                world.updateComparators(pos,this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof PokemonSpawnerBlockEntity pokemonSpawnerBlockEntity) {
                // call your function on the BlockEntity
                if(pokemonSpawnerBlockEntity.getDataReceiverBlockEntity() != null){
                    if(!pokemonSpawnerBlockEntity.getDataReceiverBlockEntity().isPowered()) {
                        if (pokemonSpawnerBlockEntity.getDataReceiverBlockEntity().getSelectedPokemon() == null) {
                            pokemonSpawnerBlockEntity.getDataReceiverBlockEntity().setSelectedPokemon(pokemonSpawnerBlockEntity.getPokemonOnBlock());
                            PokemonSpawnHelper.spawnPokemonAt(Objects.requireNonNull(world), pokemonSpawnerBlockEntity.getDataReceiverBlockEntity().getPos(), pokemonSpawnerBlockEntity.getPokemonOnBlock(),"");
                        }
                        else
                        {
                            boolean currentActive = state.get(ACTIVE);
                            world.setBlockState(pos, state.with(ACTIVE, !currentActive), Block.NOTIFY_ALL);
                            pokemonSpawnerBlockEntity.setActive(!currentActive);

                        }
                    }
                    else{
                        pokemonSpawnerBlockEntity.getDataReceiverBlockEntity().updateFlippedPokemon(pokemonSpawnerBlockEntity,world);
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Items fall through, but players/entities can still walk on it
        if (context instanceof EntityShapeContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof ItemEntity) {
                return VoxelShapes.empty(); // No collision for items
            }
        }
        return super.getCollisionShape(state, world, pos, context);
    }
}