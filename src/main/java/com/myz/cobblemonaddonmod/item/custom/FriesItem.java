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
            ServerWorld serverWorld = (ServerWorld) world;

            RegistryKey<Enchantment> lightningStrikerKey = RegistryKey.of(
                    RegistryKeys.ENCHANTMENT,
                    Identifier.of("cobblemonaddonmod", "lightning_striker")
            );

            // Get the enchantment level (returns 0 if not present)
            int enchantmentLevel = EnchantmentHelper.getLevel(
                    serverWorld.getRegistryManager()
                            .get(RegistryKeys.ENCHANTMENT)
                            .getEntry(lightningStrikerKey)
                            .orElse(null),
                    stack
            );


            switch (enchantmentLevel)
            {
                case 1:
                {
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING,20));
                    break;
                }
                case 2:
                {
                    user.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION,30));
                }
                default:
                    break;
            }

            // Get the enchantment level (returns 0 if not present)
            int efficiencyLevel = EnchantmentHelper.getLevel(
                    serverWorld.getRegistryManager()
                            .get(RegistryKeys.ENCHANTMENT)
                            .getEntry(Enchantments.EFFICIENCY)
                            .orElse(null),
                    stack
            );

            // Direction the player is looking (includes up/down)
            Vec3d look = user.getRotationVec(1.0F);
            double dashStrength = 5;

            // Apply velocity in that direction
            user.addVelocity(look.x * dashStrength, look.y * dashStrength, look.z * dashStrength);
            user.velocityModified = true;

            // Cooldown\
            switch (efficiencyLevel)
            {
                case 1:{
                    user.getItemCooldownManager().set(this, 17);
                    break;
                }
                case 2:{
                    user.getItemCooldownManager().set(this, 14);
                    break;
                }
                case 3:{
                    user.getItemCooldownManager().set(this, 10);
                    break;
                }
                case 4:{
                    user.getItemCooldownManager().set(this, 8);
                    break;
                }
                case 5:{
                    user.getItemCooldownManager().set(this, 5);
                    break;
                }
                default:
                    user.getItemCooldownManager().set(this, 20);
            }

            // Sound effect
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                    SoundCategory.PLAYERS, 1.0F, 1.0F);

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
        if (enchantmentKey.equals(Enchantments.EFFICIENCY)) {
            return true;
        }

        // Allow Mending
        if (enchantmentKey.equals(Enchantments.MENDING)) {
            return true;
        }

        // Allow Lightning Striker (your custom enchantment)
        RegistryKey<Enchantment> lightningStrikerKey = RegistryKey.of(
                RegistryKeys.ENCHANTMENT,
                Identifier.of("cobblemonaddonmod", "lightning_striker")
        );
        if (enchantmentKey.equals(lightningStrikerKey)) {
            return true;
        }

        // Deny all other enchantments
        return false;
    }
}