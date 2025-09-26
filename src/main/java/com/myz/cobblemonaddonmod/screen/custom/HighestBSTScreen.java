package com.myz.cobblemonaddonmod.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.block.entity.custom.HighestBstBlockEntity;
import com.myz.cobblemonaddonmod.screen.widget.PlayerListWidget;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HighestBSTScreen extends HandledScreen<HighestBSTScreenHandler> {

    public static final Identifier GUI_TEXTURE =
            Identifier.of(CobblemonAddonMod.MOD_ID,"textures/gui/HighestBSTChallenge/HighestBSTGui.png");
    private ButtonWidget joinLeaveButton;
    private ButtonWidget startStopButton;

    public HighestBSTScreen(HighestBSTScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;  // Standard Minecraft GUI width
        this.backgroundHeight = 166; // Standard Minecraft GUI height (for blocks with player inv)
    }

    @Override
    protected void init() {
        super.init();
        // Center the title text
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Initialize the Join/Leave button
        joinLeaveButton = ButtonWidget.builder(Text.literal("Join Game"), (button) -> {
                    this.handler.onButtonClick( client.player,0); // ID 0 for Join/Leave action
                })
                .dimensions(x + 10, y + 80, 80, 20) // Position and size
                .build();
        addDrawableChild(joinLeaveButton);

        // Initialize the Start/Stop button
        startStopButton = ButtonWidget.builder(Text.literal("Start Game"), (button) -> {
                    this.handler.onButtonClick( client.player,1); // ID 1 for Start/Stop action
                    this.close();
                })
                .dimensions(x + 95, y + 80, 70, 20) // Position and size
                .build();
        addDrawableChild(startStopButton);

        // Set initial button states
        updateButtonStates();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render the dark background behind the GUI
        renderBackground(context,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);
        // Render tooltips for hovered items/elements
        drawMouseoverTooltip(context, mouseX, mouseY);

        // Draw custom text (e.g., player count)
        context.drawText(textRenderer, "Players: " + handler.getPlayerCount(), x + 10, y + 50, 0x404040, false); // Dark grey text
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Set shader color to white to ensure the texture renders correctly
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // Draw the background texture
        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        // Update button states every tick to reflect current game state
        updateButtonStates();
    }

    // Helper method to update the text and active state of the buttons
    private void updateButtonStates() {
        // Update Join/Leave button
        if (handler.isPlayerJoined(client.player)) {
            joinLeaveButton.setMessage(Text.literal("Leave Game"));
        } else {
            joinLeaveButton.setMessage(Text.literal("Join Game"));
        }

        // Update Start/Stop button
        if (handler.isGameActive()) {
            startStopButton.setMessage(Text.literal("Stop Game"));
            startStopButton.active = true; // Can always stop an active game
        } else {
            startStopButton.setMessage(Text.literal("Start Game"));
            // Only allow starting if there are 1 or more players and the game is not active
            startStopButton.active = handler.getPlayerCount() > 0;
        }
    }
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // Only draw title text, no "Inventory"
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
    }
}
