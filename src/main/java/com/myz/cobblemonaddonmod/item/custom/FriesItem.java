package com.myz.cobblemonaddonmod.item.custom;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FriesItem extends Item {
    public FriesItem(Settings settings) {
        super(settings.maxDamage(100));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            // Direction the player is looking (includes up/down)
            Vec3d look = user.getRotationVec(1.0F);
            double dashStrength = 5; // tweak distance

            // Apply velocity in that direction
            user.addVelocity(look.x * dashStrength, look.y * dashStrength, look.z * dashStrength);
            user.velocityModified = true; // force sync with client

            // Optional cooldown
            user.getItemCooldownManager().set(this, 20);

            // Sound effect
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                    SoundCategory.PLAYERS, 1.0F, 1.0F);

            // The vanilla damage method handles the Unbreaking enchantment automatically.
            stack.damage(1,(ServerWorld) world, ((ServerPlayerEntity) user), item -> { user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND); });

        }

        return TypedActionResult.success(stack, world.isClient());
    }

    /**
     * Makes the item enchantable. The return value influences the quality of enchantments.
     * Higher values are better. Gold items have an enchantability of 22.
     * @return The enchantability value for this item.
     */
    @Override
    public int getEnchantability() {
        return 15; // A value similar to iron tools.
    }

    /**
     * Allows the item to be enchanted.
     * @param stack The ItemStack of this item.
     * @return true if the item can be enchanted, false otherwise.
     */
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }
}