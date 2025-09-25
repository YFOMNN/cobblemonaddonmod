package com.myz.cobblemonaddonmod.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HighestBSTScreen extends HandledScreen<HighestBSTScreenHandler> {

    public static final Identifier GUI_TEXTURE =
            Identifier.of(CobblemonAddonMod.MOD_ID,"textures/gui/HighestBSTChallenge/HighestBSTGui.png");

    public HighestBSTScreen(HighestBSTScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        // These must match your GUI texture size
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        // Calculate GUI's top-left corner
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        // Add Start button relative to GUI background
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Start"), b -> {
                    // TODO: Your button's action
                }).dimensions(x + 40, y + 80, 100, 20).build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Join"), b -> {
                    // TODO: Your button's action
                }).dimensions(x + 40, y + 110, 100, 20).build()
        );
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // Only draw title text, no "Inventory"
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
