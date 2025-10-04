package com.myz.cobblemonaddonmod.screen.custom;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.myz.cobblemonaddonmod.screen.ModScreenHandlers;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;

public class TeleportTargetScreenHandler extends ScreenHandler {

    private final PlayerEntity player;
    private final Hand hand;

    public TeleportTargetScreenHandler(int syncId, PlayerInventory playerInventory, Hand hand) {
        super(ModScreenHandlers.TELEPORT_TARGET_SCREEN_HANDLER, syncId);
        this.player = playerInventory.player;
        this.hand = hand;
    }

    // Called from client-side screen - now just closes the screen
    // The actual teleport request will be sent via packet
    public void requestTeleport(String targetName) {
        // This method is now handled by the packet system
        // See ModNetworking.java for the implementation
    }

    public Hand getHand() {
        return hand;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    // Server-side teleport logic (called from packet handler)
    public static void teleportToTarget(ServerPlayerEntity player, String targetName, Hand hand) {
        ServerWorld world = player.getServerWorld();
        ItemStack stack = player.getStackInHand(hand);
        Entity targetEntity = null;

        // 1. First, try to find a player by name
        ServerPlayerEntity targetPlayer = player.getServer().getPlayerManager().getPlayer(targetName);
        if (targetPlayer != null) {
            targetEntity = targetPlayer;
        } else {
            // 2. If no player is found, search for entities based on enchantment level
            RegistryKey<Enchantment> teleportTargetKey = RegistryKey.of(
                    RegistryKeys.ENCHANTMENT,
                    Identifier.of("cobblemonaddonmod", "teleport_effect")
            );

            int enchantmentLevel = EnchantmentHelper.getLevel(
                    world.getServer().getRegistryManager()
                            .get(RegistryKeys.ENCHANTMENT)
                            .getEntry(teleportTargetKey)
                            .orElse(null),
                    stack
            );

            double searchRadius = 0;
            switch (enchantmentLevel) {
                case 1:
                    searchRadius = 40;
                    break;
                case 2:
                    searchRadius = 80;
                    break;
            }

            Box searchBox = new Box(player.getBlockPos()).expand(searchRadius);
            var nearbyEntities = world.getOtherEntities(player, searchBox);

            // 3. Search for matching entities
            for (Entity entity : nearbyEntities) {
                if (entity instanceof PokemonEntity pokemonEntity) {
                    var pokemon = pokemonEntity.getPokemon();
                    var nickname = pokemon.getNickname();
                    String speciesName = pokemon.getSpecies().getName();

                    if (nickname != null && nickname.getString().equalsIgnoreCase(targetName)) {
                        targetEntity = entity;
                        break;
                    }
                    if (speciesName.equalsIgnoreCase(targetName)) {
                        targetEntity = entity;
                        break;
                    }
                }

                if (entity.hasCustomName() && entity.getCustomName().getString().equalsIgnoreCase(targetName)) {
                    targetEntity = entity;
                    break;
                }
            }
        }

        // 4. Perform the teleport if target was found
        if (targetEntity != null) {
            player.teleport(
                    (ServerWorld) targetEntity.getWorld(),
                    targetEntity.getX(),
                    targetEntity.getY(),
                    targetEntity.getZ(),
                    targetEntity.getYaw(),
                    targetEntity.getPitch()
            );

            world.playSound(
                    null,
                    player.getBlockPos(),
                    SoundEvents.ENTITY_GENERIC_EAT,
                    SoundCategory.PLAYERS,
                    1.0F, 1.0F
            );

            stack.damage(1, world, player, item -> {
                player.sendEquipmentBreakStatus(item, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            });

            player.sendMessage(Text.literal("Teleported to " + targetName), false);
        } else {
            player.sendMessage(Text.literal("Could not find '" + targetName + "' nearby."), false);
        }
    }
}