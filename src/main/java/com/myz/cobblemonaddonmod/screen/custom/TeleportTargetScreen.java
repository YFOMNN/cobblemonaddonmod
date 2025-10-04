package com.myz.cobblemonaddonmod.screen.custom;

import com.myz.cobblemonaddonmod.networking.ModClientNetworking;
import com.myz.cobblemonaddonmod.networking.ModNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TeleportTargetScreen extends HandledScreen<TeleportTargetScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of("cobblemonaddonmod", "textures/gui/HighestBSTChallenge/HighestBSTGui.png");
    private TextFieldWidget targetField;

    public TeleportTargetScreen(TeleportTargetScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        this.backgroundWidth = 176;
        this.backgroundHeight = 180;
        this.playerInventoryTitleY = Integer.MAX_VALUE;
    }

    @Override
    protected void init() {
        super.init();

        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        targetField = new TextFieldWidget(
                this.textRenderer,
                x + 30,
                y + 80,
                116,
                20,
                Text.literal("Target Name")
        );
        targetField.setMaxLength(32);
        targetField.setPlaceholder(Text.literal("Enter name..."));
        addSelectableChild(targetField);
        setInitialFocus(targetField);

        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Teleport"),
                        button -> {
                            String target = targetField.getText();
                            if (!target.isEmpty()) {
                                // Send packet to server instead of using static map
                                ModClientNetworking.sendTeleportRequest(target, handler.getHand());
                                close();
                            }
                        })
                .dimensions(x + 29, y + 120, 58, 20)
                .build()
        );

        addDrawableChild(ButtonWidget.builder(
                        Text.literal("Cancel"),
                        button -> close())
                .dimensions(x + 89, y + 120, 58, 20)
                .build()
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        targetField.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(
                this.textRenderer,
                "Teleport to Target",
                this.titleX,
                this.titleY,
                4210752,
                false
        );

        context.drawText(
                this.textRenderer,
                "Target Name:",
                30,
                75,
                4210752,
                false
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}