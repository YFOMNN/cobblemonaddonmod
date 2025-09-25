package com.myz.cobblemonaddonmod.screen.custom;

import com.myz.cobblemonaddonmod.screen.ModScreenHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class HighestBSTScreenHandler extends ScreenHandler {


    public HighestBSTScreenHandler(int syncId, PlayerInventory playerInventory,BlockPos pos)
    {
        this(syncId,playerInventory,playerInventory.player.getWorld().getBlockEntity(pos));
    }

    public HighestBSTScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandler.HIGHEST_BST_SCREEN_HANDLER_SCREEN_HANDLER,syncId);

    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
