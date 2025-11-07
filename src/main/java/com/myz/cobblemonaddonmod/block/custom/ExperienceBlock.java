package com.myz.cobblemonaddonmod.block.custom;

import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.block.entity.custom.ExperienceBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExperienceBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final MapCodec<ExperienceBlock> CODEC = ExperienceBlock.createCodec(ExperienceBlock::new);

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public ExperienceBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ExperienceBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            BlockEntity be = world.getBlockEntity(pos);

            if (be instanceof ExperienceBlockEntity levelReleaseBlockEntity) {
                long currentTime = world.getTime();
                long lastUsedTime = levelReleaseBlockEntity.getLastUsedTime();
                long cooldownTicks = 2400; // 2 minutes = 120 seconds * 20 ticks

                if (currentTime - lastUsedTime >= cooldownTicks) {
                    // Grant 10 levels
                    net.minecraft.entity.ExperienceOrbEntity.spawn(
                            (net.minecraft.server.world.ServerWorld) world,
                            net.minecraft.util.math.Vec3d.ofCenter(pos).add(0, 1, 0),
                            1010
                    );

                    // Update last used time
                    levelReleaseBlockEntity.setLastUsedTime(currentTime);
                    levelReleaseBlockEntity.markDirty();

                    // Send success message
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
        // We don't need a ticker for this block since we check cooldown on click
        return null;
    }
}