package com.myz.cobblemonaddonmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.block.entity.custom.ExperienceBlockEntity;
import com.myz.cobblemonaddonmod.block.entity.custom.SupplyCrateBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SupplyCrateBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final MapCodec<SupplyCrateBlock> CODEC = SupplyCrateBlock.createCodec(SupplyCrateBlock::new);

    // Block state property to show if crate is ready
    public static final BooleanProperty READY = BooleanProperty.of("ready");

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public SupplyCrateBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(READY, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(READY);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SupplyCrateBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof SupplyCrateBlockEntity levelReleaseBlockEntity) {
                long currentTime = world.getTime();
                long lastUsedTime = levelReleaseBlockEntity.getLastUsedTime();
                long cooldownTicks = 24000; // 20 minutes

                if (currentTime - lastUsedTime >= cooldownTicks) {
                    // Give items to player
                    serverPlayer.getInventory().insertStack(new ItemStack(Items.EGG, 64));
                    serverPlayer.getInventory().insertStack(new ItemStack(Items.WHITE_WOOL, 64));
                    serverPlayer.getInventory().insertStack(new ItemStack(Items.MILK_BUCKET, 6));

                    // Update last used time
                    levelReleaseBlockEntity.setLastUsedTime(currentTime);
                    levelReleaseBlockEntity.markDirty();

                    // Set block state to not ready
                    world.setBlockState(pos, state.with(READY, false), Block.NOTIFY_ALL);

                    // Send success message
                    serverPlayer.sendMessage(
                            Text.literal("§aReceived supplies: 64x Eggs, 64x Wool, 6x Milk Buckets!"),
                            false
                    );
                } else {
                    // Calculate remaining time
                    long remainingTicks = cooldownTicks - (currentTime - lastUsedTime);
                    long remainingSeconds = remainingTicks / 20;
                    long minutes = remainingSeconds / 60;
                    long seconds = remainingSeconds % 60;

                    // Send cooldown message
                    serverPlayer.sendMessage(
                            Text.literal("§cCooldown: " + minutes + "m " + seconds + "s remaining"),
                            false
                    );
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        // Return a ticker to check and update the ready state
        return world.isClient ? null : (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof ExperienceBlockEntity experienceBlockEntity) {
                long currentTime = world1.getTime();
                long lastUsedTime = experienceBlockEntity.getLastUsedTime();
                long cooldownTicks = 24000;

                boolean shouldBeReady = currentTime - lastUsedTime >= cooldownTicks;
                boolean currentlyReady = state1.get(READY);

                // Update block state if it changed
                if (shouldBeReady != currentlyReady) {
                    world1.setBlockState(pos, state1.with(READY, shouldBeReady), Block.NOTIFY_ALL);
                }
            }
        };
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SupplyCrateBlockEntity experienceBlockEntity) {
                long currentTime = world.getTime();
                long lastUsedTime = experienceBlockEntity.getLastUsedTime();
                long cooldownTicks = 24000;

                boolean shouldBeReady = currentTime - lastUsedTime >= cooldownTicks;
                world.setBlockState(pos, state.with(READY, shouldBeReady), Block.NOTIFY_ALL);
            }
        }
    }
}