package com.myz.cobblemonaddonmod.screen.custom;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.block.entity.custom.HighestBstBlockEntity;
import com.myz.cobblemonaddonmod.screen.ModScreenHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HighestBSTScreenHandler extends ScreenHandler {
    @Nullable
    private final HighestBstBlockEntity blockEntity;
    private final World world;

    // --- CONSTRUCTOR 1: FOR THE SERVER ---
    // This is called by your BlockEntity's createMenu method.
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

    // We add null checks here to be safe.
    public boolean isPlayerJoined(PlayerEntity player) {
        return blockEntity != null && blockEntity.isPlayerJoined(player);
    }

    public boolean isGameActive() {
        return blockEntity != null && blockEntity.isGameActive();
    }

    public int getPlayerCount() {
        return blockEntity != null ? blockEntity.getJoinedPlayers().size() : 0;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (blockEntity != null) {
            switch (id) {

                case 0: {
                    if (!isPlayerJoined(player)) {
                        player.sendMessage(Text.literal("JOINEDDDD"), true);
                        blockEntity.addPlayer(player);
                    } else {
                        player.sendMessage(Text.literal("LEFT :("), true);
                        blockEntity.removePlayer(player);
                    }
                    break;
                }
                case 1:
                {
                    if(!isGameActive()) {
                        blockEntity.startGame();
                        player.sendMessage(Text.literal("Game Started"), true);
                    }
                    else
                    {
                        blockEntity.stopGame();
                        player.sendMessage(Text.literal("Game Ended"), true);
                    }
                    break;
                }

            }
        }
        return super.onButtonClick(player, id);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        // ...
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        // We can check if the blockEntity is still valid.
        return this.blockEntity != null && !this.blockEntity.isRemoved();
    }


}