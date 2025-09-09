package com.myz.cobblemonaddonmod.block.custom;
import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.block.entity.custom.GrillBlockEntity;
import com.myz.cobblemonaddonmod.item.custom.*;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld; // Import for ServerWorld
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
// Removed java.util.logging.Level as it's not used

public class GrillBlock extends BlockWithEntity implements BlockEntityProvider {

    public static final MapCodec<GrillBlock> CODEC = GrillBlock.createCodec(GrillBlock::new);

    public GrillBlock(Settings settings)
    {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrillBlockEntity(pos,state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock())
        {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GrillBlockEntity)
            {
                net.minecraft.util.ItemScatterer.spawn(world, pos, (GrillBlockEntity) blockEntity);
                world.updateComparators(pos,this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }


    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient()) { // Only execute server-side logic
            // You can keep these print statements for debugging, or remove them once it works
            System.out.println("--- Grill Interaction ---");
            System.out.println("Item in player hand: " + stack.getItem().getName().getString());
            System.out.println("Player hand empty: " + stack.isEmpty());
            System.out.println("Player sneaking: " + player.isSneaking());


            if(world.getBlockEntity(pos) instanceof GrillBlockEntity grillBlockEntity) {
                ItemStack stackOnGrill = grillBlockEntity.getStack(0);

                // --- LOGIC 1: REPAIRING BURGER (Sneaking with empty hand) ---
                // Condition: Burger on grill, Player's hand is empty, Player IS sneaking
                if (stackOnGrill.getItem() instanceof BurgerItem && stack.isEmpty() && player.isSneaking()) {
                    System.out.println("Attempting to repair Burger.");
                    if (stackOnGrill.isDamaged()) {
                        int currentDamage = stackOnGrill.getDamage();
                        int maxDamage = stackOnGrill.getMaxDamage();
                        int repairAmount = maxDamage / 8;
                        if (repairAmount == 0) repairAmount = 1;

                        stackOnGrill.setDamage(Math.max(0, currentDamage - repairAmount));
                        world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.7F, 1.2F);

                        // Add smoke particles for repair
                        if (world instanceof ServerWorld serverWorld) { // Ensure it's a server world for spawnParticles
                            serverWorld.spawnParticles(ParticleTypes.SMOKE,
                                    pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D,
                                    5, // count
                                    0.2D, 0.2D, 0.2D, // dx, dy, dz (spread)
                                    0.05D); // speed (velocity)
                        }

                        grillBlockEntity.markDirty();
                        world.updateListeners(pos, state, state, 0);
                        System.out.println("Burger repaired! New damage: " + stackOnGrill.getDamage());
                        return ItemActionResult.SUCCESS;
                    } else {
                        System.out.println("Burger already full durability.");
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7F, 1.0F);
                        return ItemActionResult.SUCCESS; // Handled, no repair needed
                    }
                }
                else if (stackOnGrill.getItem() instanceof FriesItem && stack.isEmpty() && player.isSneaking()) {
                    System.out.println("Attempting to repair Fries.");
                    if (stackOnGrill.isDamaged()) {
                        int currentDamage = stackOnGrill.getDamage();
                        int maxDamage = stackOnGrill.getMaxDamage();
                        int repairAmount = maxDamage / 8;
                        if (repairAmount == 0) repairAmount = 1;

                        stackOnGrill.setDamage(Math.max(0, currentDamage - repairAmount));
                        world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.7F, 1.2F);

                        // Add smoke particles for repair
                        if (world instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(ParticleTypes.SMOKE,
                                    pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D,
                                    5, // count
                                    0.2D, 0.2D, 0.2D, // dx, dy, dz (spread)
                                    0.05D); // speed (velocity)
                        }

                        grillBlockEntity.markDirty();
                        world.updateListeners(pos, state, state, 0);
                        System.out.println("Fries repaired! New damage: " + stackOnGrill.getDamage());
                        return ItemActionResult.SUCCESS;
                    } else {
                        System.out.println("Fries already full durability.");
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7F, 1.0F);
                        return ItemActionResult.SUCCESS; // Handled, no repair needed
                    }
                }
                else if (stackOnGrill.getItem() instanceof WeatherMilkItem && stack.isEmpty() && player.isSneaking()) {
                    System.out.println("Attempting to repair Fries.");
                    if (stackOnGrill.isDamaged()) {
                        int currentDamage = stackOnGrill.getDamage();
                        int maxDamage = stackOnGrill.getMaxDamage();
                        int repairAmount = maxDamage / 8;
                        if (repairAmount == 0) repairAmount = 1;

                        stackOnGrill.setDamage(Math.max(0, currentDamage - repairAmount));
                        world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.7F, 1.2F);

                        // Add smoke particles for repair
                        if (world instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(ParticleTypes.SMOKE,
                                    pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D,
                                    5, // count
                                    0.2D, 0.2D, 0.2D, // dx, dy, dz (spread)
                                    0.05D); // speed (velocity)
                        }

                        grillBlockEntity.markDirty();
                        world.updateListeners(pos, state, state, 0);
                        System.out.println("Fries repaired! New damage: " + stackOnGrill.getDamage());
                        return ItemActionResult.SUCCESS;
                    } else {
                        System.out.println("Fries already full durability.");
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7F, 1.0F);
                        return ItemActionResult.SUCCESS; // Handled, no repair needed
                    }
                }
                else if (stackOnGrill.getItem() instanceof TimeSoda && stack.isEmpty() && player.isSneaking()) {
                    System.out.println("Attempting to repair Fries.");
                    if (stackOnGrill.isDamaged()) {
                        int currentDamage = stackOnGrill.getDamage();
                        int maxDamage = stackOnGrill.getMaxDamage();
                        int repairAmount = maxDamage / 8;
                        if (repairAmount == 0) repairAmount = 1;

                        stackOnGrill.setDamage(Math.max(0, currentDamage - repairAmount));
                        world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.7F, 1.2F);

                        // Add smoke particles for repair
                        if (world instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(ParticleTypes.SMOKE,
                                    pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D,
                                    5, // count
                                    0.2D, 0.2D, 0.2D, // dx, dy, dz (spread)
                                    0.05D); // speed (velocity)
                        }

                        grillBlockEntity.markDirty();
                        world.updateListeners(pos, state, state, 0);
                        System.out.println("Fries repaired! New damage: " + stackOnGrill.getDamage());
                        return ItemActionResult.SUCCESS;
                    } else {
                        System.out.println("Fries already full durability.");
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7F, 1.0F);
                        return ItemActionResult.SUCCESS; // Handled, no repair needed
                    }
                }
                else if (stackOnGrill.getItem() instanceof BenAndHadiItem && stack.isEmpty() && player.isSneaking()) {
                    System.out.println("Attempting to repair Fries.");
                    if (stackOnGrill.isDamaged()) {
                        int currentDamage = stackOnGrill.getDamage();
                        int maxDamage = stackOnGrill.getMaxDamage();
                        int repairAmount = maxDamage / 8;
                        if (repairAmount == 0) repairAmount = 1;

                        stackOnGrill.setDamage(Math.max(0, currentDamage - repairAmount));
                        world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.7F, 1.2F);

                        // Add smoke particles for repair
                        if (world instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(ParticleTypes.SMOKE,
                                    pos.getX() + 0.5D, pos.getY() + 0.7D, pos.getZ() + 0.5D,
                                    5, // count
                                    0.2D, 0.2D, 0.2D, // dx, dy, dz (spread)
                                    0.05D); // speed (velocity)
                        }

                        grillBlockEntity.markDirty();
                        world.updateListeners(pos, state, state, 0);
                        System.out.println("Fries repaired! New damage: " + stackOnGrill.getDamage());
                        return ItemActionResult.SUCCESS;
                    } else {
                        System.out.println("Fries already full durability.");
                        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.7F, 1.0F);
                        return ItemActionResult.SUCCESS; // Handled, no repair needed
                    }
                }
                // --- END REPAIR LOGIC ---

                // --- LOGIC 2: PLACING AN ITEM (Holding an item) ---
                // Condition: Grill is empty, Player IS holding an item
                else if(grillBlockEntity.isEmpty() && !stack.isEmpty()) {
                    System.out.println("Placing item on grill.");
                    grillBlockEntity.setStack(0, stack.copyWithCount(1));
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 2f);
                    player.getStackInHand(hand).decrement(1);

                    // Add flame particles for placing an item
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.FLAME,
                                pos.getX() + 0.5D, pos.getY() + 0.6D, pos.getZ() + 0.5D,
                                7, // count
                                0.3D, 0.1D, 0.3D, // dx, dy, dz (spread)
                                0.02D); // speed (velocity)
                    }

                    grillBlockEntity.markDirty();
                    world.updateListeners(pos, state, state, 0);
                    return ItemActionResult.SUCCESS;
                }
                // --- LOGIC 3: PICKING UP AN ITEM (Empty hand, NOT sneaking) ---
                // Condition: Grill has an item, Player's hand is empty, Player IS NOT sneaking
                else if(stack.isEmpty() && !player.isSneaking() && !stackOnGrill.isEmpty()) {
                    System.out.println("Picking up item from grill.");
                    ItemStack stackToGive = grillBlockEntity.getStack(0);
                    if (!player.getInventory().insertStack(stackToGive)) {
                        net.minecraft.util.ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), stackToGive);
                    }
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                    grillBlockEntity.clear();

                    // Add sparkle/flame particles for picking up an item
                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.CRIT,
                                pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D,
                                10, // count
                                0.25D, 0.15D, 0.25D, // dx, dy, dz (spread)
                                0.1D); // speed (velocity)
                        serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME,
                                pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D,
                                7, // count
                                0.2D, 0.1D, 0.2D, // dx, dy, dz (spread)
                                0.05D); // speed (velocity)
                    }

                    grillBlockEntity.markDirty();
                    world.updateListeners(pos, state, state, 0);
                    return ItemActionResult.SUCCESS;
                }
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; // No custom action was handled
    }
}