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

public class WeatherMilkItem extends Item {

    public WeatherMilkItem(Settings settings) {
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

            ServerWorld overworld = server.getWorld(World.OVERWORLD);
            if (overworld == null) {
                System.out.println("DEBUG: Overworld is null, failing use.");
                return TypedActionResult.fail(stack);
            }

            Random random = new Random();
            int choice = random.nextInt(3); // 0 = clear, 1 = rain

            System.out.println("DEBUG: Player " + user.getName().getString() + " used WeatherMilkItem. Random choice: " + choice);

            // Duration for weather (in ticks). For example, 5 minutes = 5 * 60 * 20 = 6000 ticks.
            int duration = 6000; // 5 minutes

            switch (choice) {
                case 0 -> { // Clear
                    // Arguments: clearDuration, rainDuration, thunderDuration
                    overworld.setWeather(duration, 0, false, false);
                    broadcast(server, "[Server] The weather is now clear!");
                }
                case 1 -> { // Rain
                    // Arguments: clearDuration, rainDuration, thunderDuration (last two are isRaining, isThundering)
                    overworld.setWeather(0, duration, true, false);
                    broadcast(server, "[Server] It started raining!");
                }
                // You can add a third case for thunder if you expand random.nextInt(3)
                case 2 -> { // Thunder
                     overworld.setWeather(0, duration, true, true);
                     broadcast(server, "[Server] A thunderstorm is brewing!");
                }
            }
            user.getItemCooldownManager().set(this, 100);
            stack.damage(1,(ServerWorld) world, ((ServerPlayerEntity) user), item -> { user.sendEquipmentBreakStatus(item, EquipmentSlot.MAINHAND); });
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    private void broadcast(MinecraftServer server, String msg) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(Text.literal(msg), true);
        }
    }
}