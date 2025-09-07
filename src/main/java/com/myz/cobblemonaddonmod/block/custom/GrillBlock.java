package com.myz.cobblemonaddonmod.block.custom;
import com.mojang.serialization.MapCodec;
import com.myz.cobblemonaddonmod.block.entity.custom.GrillBlockEntity;
import com.myz.cobblemonaddonmod.item.custom.BurgerItem; // Import your BurgerItem
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
                if (stackOnGrill.getItem() instanceof BurgerItem && stack.isEmpty() && player.isSneaking()) { // <-- player.isSneaking() added here
                    System.out.println("Attempting to repair Burger.");
                    if (stackOnGrill.isDamaged()) {
                        int currentDamage = stackOnGrill.getDamage();
                        int maxDamage = stackOnGrill.getMaxDamage();
                        int repairAmount = maxDamage / 8;
                        if (repairAmount == 0) repairAmount = 1;

                        stackOnGrill.setDamage(Math.max(0, currentDamage - repairAmount));
                        world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.7F, 1.2F);
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
                // --- END REPAIR LOGIC ---

                // --- LOGIC 2: PLACING AN ITEM (Holding an item) ---
                // Condition: Grill is empty, Player IS holding an item
                else if(grillBlockEntity.isEmpty() && !stack.isEmpty()) { // <-- No 'player.isSneaking()' here
                    System.out.println("Placing item on grill.");
                    grillBlockEntity.setStack(0, stack.copyWithCount(1));
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 2f);
                    player.getStackInHand(hand).decrement(1);

                    grillBlockEntity.markDirty();
                    world.updateListeners(pos, state, state, 0);
                    return ItemActionResult.SUCCESS;
                }
                // --- LOGIC 3: PICKING UP AN ITEM (Empty hand, NOT sneaking) ---
                // Condition: Grill has an item, Player's hand is empty, Player IS NOT sneaking
                else if(stack.isEmpty() && !player.isSneaking() && !stackOnGrill.isEmpty()) { // <-- !player.isSneaking() added here
                    System.out.println("Picking up item from grill.");
                    ItemStack stackToGive = grillBlockEntity.getStack(0);
                    if (!player.getInventory().insertStack(stackToGive)) {
                        net.minecraft.util.ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), stackToGive);
                    }
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
                    grillBlockEntity.clear();

                    grillBlockEntity.markDirty();
                    world.updateListeners(pos, state, state, 0);
                    return ItemActionResult.SUCCESS;
                }
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION; // No custom action was handled
    }
}