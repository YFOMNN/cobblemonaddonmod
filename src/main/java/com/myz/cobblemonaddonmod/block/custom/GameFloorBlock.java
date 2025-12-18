package com.myz.cobblemonaddonmod.block.custom;

import com.cobblemon.mod.common.CobblemonItems;
import com.cobblemon.mod.common.item.PokeBallItem;
import com.cobblemon.mod.common.pokeball.PokeBall;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GameFloorBlock extends Block {
    public GameFloorBlock(Settings settings) {
        super(settings.noCollision());
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity instanceof ItemEntity itemEntity) {
                // Check if it's a Master Ball
                ItemStack stack = itemEntity.getStack();
                if (stack.isOf(CobblemonItems.MASTER_BALL)) { // Replace with your Master Ball item
                    return VoxelShapes.fullCube(); // Master Balls have collision
                }
                return VoxelShapes.empty(); // Other items fall through
            }
        }
        return VoxelShapes.fullCube(); // Full collision for players/mobs
    }
    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && entity instanceof ItemEntity) {
            entity.discard();
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS, 0.5f, 2.6f);
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}
