package com.myz.cobblemonaddonmod;

import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.block.entity.renderer.GrillBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class CobblemonAddonModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
       BlockEntityRendererFactories.register(ModBlockEntities.GRILL_RE, GrillBlockEntityRenderer::new);
    }
}
