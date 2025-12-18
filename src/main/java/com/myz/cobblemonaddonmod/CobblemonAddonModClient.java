package com.myz.cobblemonaddonmod;

import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.block.entity.renderer.GrillBlockEntityRenderer;
import com.myz.cobblemonaddonmod.item.ModItems;
import com.myz.cobblemonaddonmod.screen.ModScreenHandlers;
import com.myz.cobblemonaddonmod.screen.custom.TeleportTargetScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;

public class CobblemonAddonModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntities.GRILL_RE, GrillBlockEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.TELEPORT_TARGET_SCREEN_HANDLER, TeleportTargetScreen::new);
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            // tintIndex 0 = base layer (no tint)
            // tintIndex 1+ = overlay layers (apply dye)
            return tintIndex < 1 ? -1 : DyedColorComponent.getColor(stack, 0xFFFFFF);
        }, ModItems.FRIES);
    }
}
