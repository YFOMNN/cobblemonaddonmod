package com.myz.cobblemonaddonmod.block.entity.renderer;

import com.myz.cobblemonaddonmod.block.entity.custom.GrillBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.WordPackedArray;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class GrillBlockEntityRenderer implements BlockEntityRenderer<GrillBlockEntity> {

    public GrillBlockEntityRenderer(BlockEntityRendererFactory.Context context){

    }

    @Override
    public void render(GrillBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = entity.getStack(0);
        if (entity.getWorld() == null) { // Add a null check for the world
            return;
        }
        if (stack.isEmpty()) {
            return; // <--- IMPORTANT: Do NOT push/pop matrices if nothing will be rendered!
        }
        matrices.push();
        matrices.translate(0.5f,1.05f,0.5f);
        matrices.scale(0.5f,0.5f,0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
        BlockPos blockPos = entity.getPos();
        int combinedLight = LightmapTextureManager.pack(entity.getWorld().getLightLevel(blockPos.up()), entity.getWorld().getLightLevel(blockPos)); // This 'light' parameter is the combined light for the block entity itself

        itemRenderer.renderItem(stack, ModelTransformationMode.FIXED    , combinedLight, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);

        matrices.pop();
    }

    private int getLight(World world, BlockPos pos)
    {
        int bLight = world.getLightLevel(LightType.BLOCK,pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);
        return LightmapTextureManager.pack(bLight, sLight);
    }
}
