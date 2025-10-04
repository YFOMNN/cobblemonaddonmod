package com.myz.cobblemonaddonmod.item.custom;

import com.myz.cobblemonaddonmod.enchantment.ModEnchantmentEffects;
import com.myz.cobblemonaddonmod.screen.custom.TeleportTargetScreenHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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
        ItemStack stack = user.getStackInHand(hand);

        // Check if item has teleport targeting enchantment
        if (!world.isClient() && hasTeleportTargeting(stack)) {

            user.openHandledScreen(new NamedScreenHandlerFactory() {
                @Override
                public Text getDisplayName() {
                    return stack.getName();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    return new com.myz.cobblemonaddonmod.screen.custom.TeleportTargetScreenHandler(syncId, playerInventory, hand);
                }
            });
            return TypedActionResult.success(stack);
        }

        user.setCurrentHand(hand); // start eating animation
        return TypedActionResult.consume(stack);
    }

    private boolean hasTeleportTargeting(ItemStack stack) {
        RegistryKey<Enchantment> teleportTargetKey = RegistryKey.of(
                RegistryKeys.ENCHANTMENT,
                Identifier.of("cobblemonaddonmod", "teleport_effect")
        );


        return EnchantmentHelper.getEnchantments(stack).getEnchantments()
                .stream()
                .anyMatch(entry -> entry.matchesKey(teleportTargetKey));
    }

    // Eating animation length
    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 32; // 32 ticks (~1.6 seconds)
    }

    // Called when player releases right-click (or after full use time)
    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!world.isClient() && user instanceof ServerPlayerEntity player) {

            // Skip if has targeting enchantment (handled by GUI)
            if (hasTeleportTargeting(stack)) {
                return;
            }

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
            stack.damage(1, (ServerWorld) world, player, item -> {
                user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND);
            });
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return stack.getMaxDamage() > 0;
    }

    @Override
    public int getEnchantability() {
        return 14; // same as iron tools
    }

}