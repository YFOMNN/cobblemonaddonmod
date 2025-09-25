package com.myz.cobblemonaddonmod.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.swing.*;

public class HighestBSTScreen extends HandledScreen<HighestBSTScreenHandler> {

    public static final Identifier GUI_TEXTURE =
            Identifier.of(CobblemonAddonMod.MOD_ID,"textures/gui/HighestBSTChallenge/HighestBSTGui.png");
    public HighestBSTScreen(HighestBSTScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0,GUI_TEXTURE);

        int x = (width -backgroundWidth)/2;
        int y = (height - backgroundHeight)/2;

        context.drawTexture(GUI_TEXTURE,x,y,0,0,backgroundWidth,backgroundHeight);

        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Start"), b -> {
                }).dimensions(86, 86, 100, 20).build()
        );
    }
}
