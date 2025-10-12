package com.myz.cobblemonaddonmod.item.custom;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.command.EntitySelectorReader.RANDOM;

public class BottleCapItem extends Item {
    public BottleCapItem(Settings settings) {
        super(settings.maxCount(12));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        // Check if the entity is a Pokémon
        if (entity instanceof PokemonEntity pokemonEntity) {
            if (!user.getWorld().isClient) {
                Pokemon pokemon = pokemonEntity.getPokemon();
                Random random = new Random();
                // Now you have access to the Pokémon entity
                // Example: Get the Pokémon's species
                Stats[] allStats = {
                        Stats.HP,
                        Stats.ATTACK,
                        Stats.DEFENCE,
                        Stats.SPECIAL_ATTACK,
                        Stats.SPECIAL_DEFENCE,
                        Stats.SPEED
                };
                String speciesName = pokemonEntity.getPokemon().getSpecies().getName();

                List<Stats> eligibleStats = new ArrayList<>();
                for (Stats stat : allStats) {
                    if (pokemon.getIvs().getOrDefault(stat) < 31) {
                        eligibleStats.add(stat);
                    }
                }
                // Check if all IVs are already maxed
                if (eligibleStats.isEmpty()) {
                    user.sendMessage(Text.literal("All IVs are already maxed out!"), false);
                    return ActionResult.FAIL;
                }


                Stats randomStat = eligibleStats.get(random.nextInt(eligibleStats.size()));
                // Set the IV to 31
                pokemon.getIvs().set(randomStat, 31);
                // Send a message to the player
                user.sendMessage(Text.literal("You used a Bottle Cap on " + speciesName + "!"), false);

                // Access the Pokémon object for more details
                // You can now manipulate IVs, EVs, level, etc.
                // Example: pokemon.getIvs().set(Stats.HP, 31);
                stack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

}
