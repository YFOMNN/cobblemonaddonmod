package com.myz.cobblemonaddonmod.item.custom;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.text.Text;


public class FriesItem extends Item {
    public FriesItem(Settings settings) {
        super(settings.maxDamage(100));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            int lightningLevel = EnchantmentHelper.getLevel(
                    world.getRegistryManager()
                            .get(RegistryKeys.ENCHANTMENT)
                            .getEntry(LIGHTNING_STRIKER_KEY)
                            .orElse(null),
                    stack
            );

            // Check if enchantment is present
            if (lightningLevel > 0) {

                // Enchantment IS present
                // Do different things based on level
                if (lightningLevel == 1) {
                    // Level 1 logic
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING,25));
                } else if (lightningLevel == 2) {
                    // Level 2 logic
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION,35));
                }
            }
            // Direction the player is looking (includes up/down)
            Vec3d look = user.getRotationVec(1.0F);
            double dashStrength = 5;

            // Apply velocity in that direction
            user.addVelocity(look.x * dashStrength, look.y * dashStrength, look.z * dashStrength);
            user.velocityModified = true;

            // Cooldown
            user.getItemCooldownManager().set(this, 20);

            // Sound effect
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                    SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Damage item (Unbreaking works automatically)
            stack.damage(1, (ServerWorld) world, (ServerPlayerEntity) user,
                    item -> user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxDamage() > 0;
    }

    @Override
    public int getEnchantability() {
        return 14; // same as iron tools
    }
    // Define your custom enchantment key
    public static final RegistryKey<Enchantment> GLIDE_ENCHANTMENT_KEY =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("cobblemonaddonmod", "glide"));


    public static final RegistryKey<Enchantment> LIGHTNING_STRIKER_KEY =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("cobblemonaddonmod", "lightning_striker"));

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
        return enchantment.matchesKey(Enchantments.UNBREAKING)
                || enchantment.matchesKey(Enchantments.MENDING) // ✅ now also allows Glide
                || enchantment.matchesKey(LIGHTNING_STRIKER_KEY); // ✅ Allow lightning striker

    }
}
