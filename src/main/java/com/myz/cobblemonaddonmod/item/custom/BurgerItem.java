package com.myz.cobblemonaddonmod.item.custom;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.List;

public class BurgerItem extends Item {

    public BurgerItem(Settings settings) {
        super(settings.maxDamage(16)); // durability: 16 uses
    }

    // Called when player right-clicks the item
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand); // start eating animation
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    // Eating animation length
    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32; // 32 ticks (~1.6 seconds)
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT; // plays eating animation
    }

    // Called when player releases right-click (or after full use time)
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient() && user instanceof ServerPlayerEntity player) {

            // Teleport to random other player
            List<ServerPlayerEntity> players = world.getServer().getPlayerManager().getPlayerList();
            if (players.size() > 1) {
                ServerPlayerEntity target;
                do {
                    target = players.get(world.random.nextInt(players.size()));
                } while (target == player);

                player.teleport(
                        target.getServerWorld(),
                        target.getX(), target.getY(), target.getZ(),
                        target.getYaw(), target.getPitch()
                );
            }

            // Play eating sound
            world.playSound(null, user.getBlockPos(),
                    SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Damage durability
            stack.damage(1,(ServerWorld) world, ((ServerPlayerEntity) user), item -> { user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND); });
        }
    }
}
