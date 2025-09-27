package com.myz.cobblemonaddonmod.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myz.cobblemonaddonmod.CheckBstPacket;
import com.myz.cobblemonaddonmod.JoinLeaveGamePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class HighestBSTScreen extends HandledScreen<HighestBSTScreenHandler> {

    public static final Identifier GUI_TEXTURE = Identifier.of("cobblemonaddonmod", "textures/gui/HighestBSTChallenge/HighestBSTGui.png");
    private ButtonWidget joinLeaveButton;
    private ButtonWidget checkBstButton;

    public HighestBSTScreen(HighestBSTScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        BlockPos blockPos = this.handler.getBlockPos();
        if (blockPos == null) return;

        joinLeaveButton = ButtonWidget.builder(Text.literal("Join"), (button) ->
                ClientPlayNetworking.send(new JoinLeaveGamePacket(blockPos))
        ).dimensions(x + 8, y + 100, 78, 20).build();
        addDrawableChild(joinLeaveButton);

        checkBstButton = ButtonWidget.builder(Text.literal("Check BST"), (button) -> {
            ClientPlayNetworking.send(new CheckBstPacket(blockPos));
            this.close(); // Close the screen after checking.
        }).dimensions(x + 89, y + 100, 78, 20).build();
        addDrawableChild(checkBstButton);

        updateButtonStates();
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        updateButtonStates();
    }

    private void updateButtonStates() {
        if (handler.isPlayerJoined(client.player)) {
            joinLeaveButton.setMessage(Text.literal("Leave"));
        } else {
            joinLeaveButton.setMessage(Text.literal("Join"));
        }
        // Only allow checking if there are at least two players.
        //checkBstButton.active = handler.getPlayerCount() >= 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);

        // This is a simplified way to show player count, as syncing full names to the client is complex.
        String playersText = "Players Joined: " + handler.getPlayerCount();
        context.drawText(textRenderer, playersText, x + 8, y + 80, 0x404040, false);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
    }
}