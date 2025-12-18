package com.myz.cobblemonaddonmod.item.custom;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
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
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FriesItem extends Item  {
    private static final int DEFAULT_COLOR = 0xFFD700; // Gold/yellow color for fries

    public FriesItem(Settings settings) {
        super(settings.maxDamage(100));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            ServerWorld serverWorld = (ServerWorld) world;

            // --- Custom Enchantment Logic ---
            RegistryKey<Enchantment> lightningStrikerKey = RegistryKey.of(
                    RegistryKeys.ENCHANTMENT,
                    Identifier.of("cobblemonaddonmod", "lightning_striker")
            );
            int enchantmentLevel = EnchantmentHelper.getLevel(
                    serverWorld.getRegistryManager()
                            .get(RegistryKeys.ENCHANTMENT)
                            .getEntry(lightningStrikerKey)
                            .orElse(null),
                    stack
            );

            // Handle Lightning Striker enchantment levels
            switch (enchantmentLevel) {
                case 1 -> user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 20));
                case 2 -> user.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 30));
                case 3 -> {
                    // Rocket launch behavior for Lightning Striker 3
                    // Apply continuous thrust over time like a real firework rocket
                    int rocketDuration = 20; // Duration in ticks (20 ticks = 1 second for level 1 rocket)
                    double thrustPerTick = 0.1; // Thrust applied each tick

                    // Get the player's current look direction for initial boost
                    Vec3d lookDirection = user.getRotationVector();

                    // Apply initial boost
                    Vec3d initialBoost = lookDirection.multiply(0.3);
                    user.addVelocity(initialBoost.getX(), initialBoost.getY(), initialBoost.getZ());

                    // Schedule continuous thrust over the duration
                    for (int i = 1; i <= rocketDuration; i++) {
                        final int tickDelay = i;
                        serverWorld.getServer().execute(() -> {
                            serverWorld.getServer().send(new net.minecraft.server.ServerTask(
                                    serverWorld.getServer().getTicks() + tickDelay,
                                    () -> {
                                        if (user.isAlive() && !user.isRemoved()) {
                                            // Get current look direction each tick for steering
                                            Vec3d currentLookDirection = user.getRotationVector();
                                            Vec3d thrust = currentLookDirection.multiply(thrustPerTick);
                                            user.addVelocity(thrust.getX(), thrust.getY(), thrust.getZ());
                                            user.velocityModified = true;

                                            if (user instanceof ServerPlayerEntity serverPlayer) {
                                                serverPlayer.networkHandler.sendPacket(
                                                        new net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket(serverPlayer)
                                                );
                                            }
                                        }
                                    }
                            ));
                        });
                    }

                    user.velocityModified = true;
                    if (user instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.networkHandler.sendPacket(
                                new net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket(serverPlayer)
                        );
                    }

                    world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH,
                            SoundCategory.PLAYERS, 1.0F, 1.0F);

                    // Skip the normal dash logic when Lightning Striker 3 is active
                    user.getItemCooldownManager().set(this, 20);
                    stack.damage(1, (ServerWorld) world, (ServerPlayerEntity) user,
                            item -> user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND));
                    return TypedActionResult.success(stack, world.isClient());
                }
                default -> {
                }
            }

            // --- DASHING LOGIC (only runs if not Lightning Striker 3) ---

            // 1. Get Power Level and keep your original strength values.
            int powerLevel = EnchantmentHelper.getLevel(
                    serverWorld.getRegistryManager()
                            .get(RegistryKeys.ENCHANTMENT)
                            .getEntry(Enchantments.POWER)
                            .orElse(null),
                    stack
            );
            double dashStrength;
            switch (powerLevel) {
                case 1 -> dashStrength = 1.5;
                case 2 -> dashStrength = 2;
                case 3 -> dashStrength = 5;
                case 4 -> dashStrength = 10;
                case 5 -> dashStrength = 20;
                default -> dashStrength = 1.25;
            }

            // 2. Get the player's raw look direction.
            Vec3d rawLookDirection = user.getRotationVector();
            Vec3d controlledDirection = new Vec3d(
                    rawLookDirection.getX(),
                    rawLookDirection.getY(),
                    rawLookDirection.getZ()
            ).normalize();
            Vec3d dashVelocity = controlledDirection.multiply(dashStrength);
            user.setVelocity(new Vec3d(0,0,0));//trying out
            user.addVelocity(dashVelocity.getX(), dashVelocity.getY(), dashVelocity.getZ());
            user.velocityModified = true;
            if (user instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.networkHandler.sendPacket(
                        new net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket(serverPlayer)
                );
            }
            user.getItemCooldownManager().set(this, 20);

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
        if (enchantmentKey.equals(Enchantments.POWER)) {
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