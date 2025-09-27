package com.myz.cobblemonaddonmod.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;

import java.util.List;

public class PlayerListWidget extends ElementListWidget<PlayerListWidget.PlayerEntry> {

    public PlayerListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom);
    }

    public void setPlayers(java.util.List<String> names) {
        this.clearEntries();
        for (String name : names) {
            this.addEntry(new PlayerEntry(name));
        }
    }

    public static class PlayerEntry extends ElementListWidget.Entry<PlayerEntry> {
        private final String name;

        public PlayerEntry(String name) {
            this.name = name;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawText(MinecraftClient.getInstance().textRenderer,
                    name, x + 5, y + 5, 0xFFFFFF, false);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of();
        }

        @Override
        public List<? extends Element> children() {
            return List.of();
        }
    }
}
