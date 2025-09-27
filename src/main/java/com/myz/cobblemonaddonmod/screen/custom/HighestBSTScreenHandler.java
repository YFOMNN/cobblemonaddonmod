package com.myz.cobblemonaddonmod.screen.custom;

import com.myz.cobblemonaddonmod.block.entity.custom.HighestBstBlockEntity;
import com.myz.cobblemonaddonmod.screen.ModScreenHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HighestBSTScreenHandler extends ScreenHandler {
    @Nullable
    private final HighestBstBlockEntity blockEntity;
    private final World world;

    public HighestBSTScreenHandler(int syncId, PlayerInventory playerInventory, HighestBstBlockEntity blockEntity) {
        super(ModScreenHandler.HIGHEST_BST_SCREEN_HANDLER_SCREEN_HANDLER, syncId);
        this.world = playerInventory.player.getWorld();
        this.blockEntity = blockEntity;

    }

    // --- CONSTRUCTOR 2: FOR THE CLIENT ---
    // This is the new one, called by your ScreenHandlerType registration.
    public HighestBSTScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(ModScreenHandler.HIGHEST_BST_SCREEN_HANDLER_SCREEN_HANDLER, syncId);
        this.world = playerInventory.player.getWorld();

        // On the client, we look up the BlockEntity from the world using the pos from the packet.
        BlockEntity be = this.world.getBlockEntity(pos);
        if (be instanceof HighestBstBlockEntity) {
            this.blockEntity = (HighestBstBlockEntity) be;
        } else {
            // This can happen if the block is broken while the GUI is opening.
            // We set it to null to prevent crashes.
            this.blockEntity = null;
        }

    }

    public boolean isPlayerJoined(PlayerEntity player) {
        return blockEntity != null && blockEntity.isPlayerJoined(player);
    }

    public List<String> getJoinedPlayerNames() {
        List<String> names = new ArrayList<>();
        if (blockEntity != null && blockEntity.getWorld() != null && !blockEntity.getWorld().isClient()) {
            MinecraftServer server = blockEntity.getWorld().getServer();
            if (server != null) {
                for (UUID uuid : blockEntity.getJoinedPlayers()) {
                    PlayerEntity player = server.getPlayerManager().getPlayer(uuid);
                    if (player != null) {
                        names.add(player.getName().getString());
                    }
                }
            }
        }
        return names;
    }

    public int getPlayerCount() {
        return blockEntity != null ? blockEntity.getJoinedPlayers().size() : 0;
    }

    public BlockPos getBlockPos() {
        return blockEntity != null ? blockEntity.getPos() : null;
    }

    @Override public boolean canUse(PlayerEntity player) { return this.blockEntity != null && !this.blockEntity.isRemoved(); }
    @Override public ItemStack quickMove(PlayerEntity player, int invSlot) { return ItemStack.EMPTY; }
}