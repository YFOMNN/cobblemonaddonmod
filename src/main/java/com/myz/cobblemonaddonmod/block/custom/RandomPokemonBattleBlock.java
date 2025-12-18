package com.myz.cobblemonaddonmod.block.custom;

import com.cobblemon.mod.common.CobblemonItems;
import com.myz.cobblemonaddonmod.PokemonSpawnHelper;
import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.custom.DataReceiverBlockEntity;
import com.myz.cobblemonaddonmod.block.entity.custom.PokemonSpawnerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RandomPokemonBattleBlock extends Block {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;


    public RandomPokemonBattleBlock(Settings settings) {
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
            int radius = 7; // how far around to check
            List<BlockPos> foundPositions = new ArrayList<>();

            for (BlockPos checkPos : BlockPos.iterate(pos.add(-radius, -radius, -radius),
                    pos.add(radius, radius, radius))) {
                if (world.getBlockState(checkPos).getBlock() == ModBlocks.POKEMON_SPAWNWER_BLOCK) {
                    foundPositions.add(checkPos.toImmutable());
                    PokemonSpawnHelper.clearPokemonAtSpawner(Objects.requireNonNull(world.getServer()).getOverworld(),checkPos);
                }
            }


            if(foundPositions.size() > 1)
            {
                for(BlockPos bp: foundPositions)
                {
                    BlockEntity be = world.getBlockEntity(bp);
                    if(be instanceof PokemonSpawnerBlockEntity pokemonSpawnerBlockEntity){
                        PokemonSpawnHelper.spawnCatchablePokemonAt(Objects.requireNonNull(world), be.getPos(), PokemonSpawnHelper.pickPokemon(true),20,player);
                    }
                }

                // Find and give master balls to the two closest players
                giveMasterBallsToClosestPlayers(world);
            }
        }
        return ActionResult.SUCCESS;
    }

    private void giveMasterBallsToClosestPlayers(World world) {
        if (world instanceof ServerWorld serverWorld) {
            List<ServerPlayerEntity> players = serverWorld.getServer().getPlayerManager().getPlayerList();

            if (players.isEmpty()) {
                return; // No players online
            }

            // If only 1 player, give them 3 master balls
            if (players.size() == 1) {
                ServerPlayerEntity singlePlayer = players.get(0);
                ItemStack masterBalls = new ItemStack(CobblemonItems.MASTER_BALL, 1);

                singlePlayer.giveItemStack(masterBalls);
                singlePlayer.sendMessage(Text.literal("§aYou received 3 Master Balls for being the only player!"), false);
                return;
            }

            ServerPlayerEntity player1 = null;
            ServerPlayerEntity player2 = null;
            double minDistance = Double.MAX_VALUE;

            // Find the two closest players
            for (int i = 0; i < players.size(); i++) {
                for (int j = i + 1; j < players.size(); j++) {
                    ServerPlayerEntity p1 = players.get(i);
                    ServerPlayerEntity p2 = players.get(j);

                    // Skip if players are in different dimensions
                    if (!p1.getWorld().getRegistryKey().equals(p2.getWorld().getRegistryKey())) {
                        continue;
                    }

                    Vec3d pos1 = p1.getPos();
                    Vec3d pos2 = p2.getPos();
                    double distance = pos1.distanceTo(pos2);

                    if (distance < minDistance) {
                        minDistance = distance;
                        player1 = p1;
                        player2 = p2;
                    }
                }
            }

            // Give master balls to the two closest players
            if (player1 != null && player2 != null) {
                ItemStack masterBall1 = new ItemStack(CobblemonItems.MASTER_BALL, 1);
                ItemStack masterBall2 = new ItemStack(CobblemonItems.MASTER_BALL, 1);

                player1.giveItemStack(masterBall1);
                player2.giveItemStack(masterBall2);

                // Send feedback messages
                player1.sendMessage(Text.literal("§aYou received a Master Ball for being one of the closest players!"), false);
                player2.sendMessage(Text.literal("§aYou received a Master Ball for being one of the closest players!"), false);

                // Optionally broadcast to all players
                serverWorld.getServer().getPlayerManager().broadcast(
                        Text.literal(String.format("§e%s §aand §e%s §awere the closest players and received Master Balls!",
                                player1.getName().getString(),
                                player2.getName().getString())),
                        false
                );
            }
        }
    }
}