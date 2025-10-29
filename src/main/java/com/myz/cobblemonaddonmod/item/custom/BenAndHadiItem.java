package com.myz.cobblemonaddonmod.item.custom;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BenAndHadiItem extends Item {

    public static List<String> hadiPhrases = new ArrayList<>(List.of("Baka", "I dont have as much time as you guys.", "Coz I am doing engineering..", "Ben and jerry's is so good!!!!","I have no words..","Try Genshin!","I don't have 1000 hours in rocket league so I don't know what a stocktane is!"));

    public BenAndHadiItem(Settings settings) {
        super(settings.maxDamage(16));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // instant kill
            Random random = new Random();
            world.getServer().getPlayerManager().broadcast(
                    Text.literal(hadiPhrases.get(random.nextInt(hadiPhrases.size()))),
                    true // false = chat, true = action bar
            );
            stack.damage(1,(ServerWorld) world, ((ServerPlayerEntity) user), item -> { user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND); });
            serverPlayer.kill();

        }

        // success so client plays hand animation
        return TypedActionResult.success(stack, world.isClient);
    }

/*
    // Called when player releases right-click (or after full use time)
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient() && user instanceof ServerPlayerEntity player)
        {


            player.kill();
        }
    }*/

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        // Get the enchantment's registry key
        RegistryKey<Enchantment> enchantmentKey = enchantment.getKey().orElse(null);

        if (enchantmentKey == null) {
            return false;
        }

        // Allow Unbreaking
        if (enchantmentKey.equals(Enchantments.UNBREAKING)) {
            return true;
        }
        // Allow Mending
        if (enchantmentKey.equals(Enchantments.MENDING)) {
            return true;
        }

        // Deny all other enchantments
        return false;
    }
}
