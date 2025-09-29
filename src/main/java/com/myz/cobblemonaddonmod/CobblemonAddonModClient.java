package com.myz.cobblemonaddonmod;

import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.block.entity.renderer.GrillBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class CobblemonAddonModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
       BlockEntityRendererFactories.register(ModBlockEntities.GRILL_RE, GrillBlockEntityRenderer::new);
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DATA_RECEIVER, RenderLayer.getCutoutMipped());
    }
}
