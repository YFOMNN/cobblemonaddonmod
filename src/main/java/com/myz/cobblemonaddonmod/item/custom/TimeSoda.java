package com.myz.cobblemonaddonmod.item.custom;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;

public class TimeSoda extends Item {
    public TimeSoda(Settings settings) {
        super(settings.maxDamage(10));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient && world instanceof ServerWorld serverWorld) {

            MinecraftServer server = serverWorld.getServer();
            if (server == null) {
                System.out.println("DEBUG: Server is null, failing use.");
                return TypedActionResult.fail(stack);
            }

            // You can choose to affect only the current world the player is in,
            // or specifically the Overworld, or all loaded worlds.
            // For simplicity, let's affect the Overworld like the WeatherMilkItem.
            ServerWorld targetWorld = server.getWorld(World.OVERWORLD);

            if (targetWorld == null) {
                System.out.println("DEBUG: Target world is null, failing use.");
                return TypedActionResult.fail(stack);
            }

            Random random = new Random();
            int choice = random.nextInt(3); // 0 = Day, 1 = Sunset/Dusk, 2 = Night

            System.out.println("DEBUG: Player " + user.getName().getString() + " used TimeOrbItem. Random choice: " + choice);

            long newTime;
            String timeMessage;

            switch (choice) {
                case 0 -> { // Day (e.g., 1000 ticks into the day)
                    newTime = 1000;
                    timeMessage = "[Server] The time has been set to Day!";
                }
                case 1 -> { // Sunset/Dusk (e.g., around 13000 ticks)
                    newTime = 13000;
                    timeMessage = "[Server] The sun begins to set...";
                }
                case 2 -> { // Night (e.g., around 18000 ticks)
                    newTime = 18000;
                    timeMessage = "[Server] The moon now shines bright!";
                }
                default -> { // Fallback, shouldn't happen with nextInt(3)
                    newTime = targetWorld.getTimeOfDay(); // Keep current time
                    timeMessage = "[Server] The time seems unchanged.";
                }
            }

            // Set the time of day for the target world
            targetWorld.setTimeOfDay(newTime);
            broadcast(server, timeMessage);
            user.getItemCooldownManager().set(this, 100);
            stack.damage(1,(ServerWorld) world, ((ServerPlayerEntity) user), item -> { user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND); });

        }

        return TypedActionResult.success(stack, world.isClient());
    }

    private void broadcast(MinecraftServer server, String msg) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.literal(msg), true); // true = action bar
        }
    }
}
