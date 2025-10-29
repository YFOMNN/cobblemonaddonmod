package com.myz.cobblemonaddonmod;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.CobblemonEntities;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonPropertyExtractor;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.storage.StoreCoordinates;
import com.cobblemon.mod.common.entity.pokemon.PokemonBehaviourFlag;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.OriginalTrainerType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;
import com.cobblemon.mod.common.pokemon.status.PersistentStatus;
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.command.EntitySelectorReader.RANDOM;

public class PokemonSpawnHelper {
    public static void spawnPokemonAt(World world, BlockPos pos, String pokemonName, String extraflag) {

        ServerWorld serverWorld = (ServerWorld) world;
// Build Pokémon data
        PokemonProperties props = PokemonProperties.Companion.parse(pokemonName.replaceAll("\\s+","") + " level=1 " + extraflag);

        Pokemon pokemonData = props.create();

// Step 2: create the entity with this Pokémon
        PokemonEntity entity = new PokemonEntity(
                serverWorld,
                pokemonData,
                CobblemonEntities.POKEMON
        );

        entity.refreshPositionAndAngles(
                pos.getX() + 0.5,
                pos.getY() + 1,
                pos.getZ() + 0.5,
                serverWorld.random.nextFloat() * 360F,
                0F
        );
        entity.setInvulnerable(true);

        entity.noClip = true;
// Step 3: check size
        float width  = entity.getWidth();
        float height = entity.getHeight();
        float maxSize = 2.0F;

        if (width > maxSize || height > maxSize) {
            float scaleFactor = Math.min(maxSize / width, maxSize / height);

            // Adjust Pokémon’s internal scale modifier
            pokemonData.setScaleModifier(pokemonData.getScaleModifier() * scaleFactor);

            // Re-apply Pokémon data into entity so size updates
            entity.setPokemon(pokemonData);
        }

        // Step 4: make uncatchable

        // Step 5: spawn
        entity.setAiDisabled(true);
        // Spawn into world
        serverWorld.spawnEntity(entity);


    }

    public static void spawnCatchablePokemonAt(MinecraftServer server, BlockPos pos, String pokemonName) {
        // Create a command source with OP level
        ServerCommandSource source = server.getCommandSource()
                .withLevel(4) // Full permissions
                .withPosition(Vec3d.ofCenter(pos).add(0,0.5f,0)); // Use the spawn position as command "location"

        // Build the command string
        String command = "spawnpokemon " + pokemonName + " noai";
        // Execute the command as the server
        server.getCommandManager().executeWithPrefix(source, command);
    }

    public static void spawnCatchablePokemonAt(World world, BlockPos pos, String pokemonName,float shinyChance) {
        List<String> ivStats = new ArrayList<>();
        ivStats.add(" hp_iv=31");
        ivStats.add(" attack_iv=31");
        ivStats.add(" special_attack_iv=31");
        ivStats.add(" defence_iv=31");
        ivStats.add(" special_defence_iv=31");
        ivStats.add(" speed_iv=31");

        // 2. Randomly shuffle the list.
        Collections.shuffle(ivStats);

        ServerWorld serverWorld = (ServerWorld) world;
// Build Pokémon data
        PokemonProperties props;
        Random random = new Random();
        int chance = random.nextInt(100);
        int selectedIV;
        if(chance < 100-shinyChance)
            props = PokemonProperties.Companion.parse(pokemonName.replaceAll("\\s+","") + " level=50 ");
        else
        {
            int perfectIVchance = random.nextInt(100);
            if (perfectIVchance < 50) {
                props = PokemonProperties.Companion.parse(pokemonName.replaceAll("\\s+", "") + " level=50 shiny");
            }
            else if (perfectIVchance < 70) {
                props = PokemonProperties.Companion.parse(pokemonName.replaceAll("\\s+", "") + " level=50 shiny "+ ivStats.get(0));
            }
            else if (perfectIVchance < 85) {
                props = PokemonProperties.Companion.parse(pokemonName.replaceAll("\\s+", "") + " level=50 shiny"+ivStats.get(1)+ivStats.get(0));
            }
            else if (perfectIVchance < 95) {
                props = PokemonProperties.Companion.parse(pokemonName.replaceAll("\\s+", "") + " level=50 shiny" + ivStats.get(0)  + ivStats.get(1)+ ivStats.get(2)+ ivStats.get(3));
            }
            else{
                props = PokemonProperties.Companion.parse(pokemonName.replaceAll("\\s+", "") + " level=50 shiny " + ivStats.get(0)  + ivStats.get(1)+ ivStats.get(2)+ ivStats.get(3)+ ivStats.get(4)+ ivStats.get(5));
            }
        }
        Pokemon pokemonData = props.create();

// Step 2: create the entity with this Pokémon
        PokemonEntity entity = new PokemonEntity(
                serverWorld,
                pokemonData,
                CobblemonEntities.POKEMON
        );

        entity.refreshPositionAndAngles(
                pos.getX() + 0.5,
                pos.getY() + 1,
                pos.getZ() + 0.5,
                serverWorld.random.nextFloat() * 360F,
                0F
        );
        entity.setInvulnerable(true);

// Step 3: check size
        float width  = entity.getWidth();
        float height = entity.getHeight();
        float maxSize = 2.0F;

        if (width > maxSize || height > maxSize) {
            float scaleFactor = Math.min(maxSize / width, maxSize / height);

            // Adjust Pokémon’s internal scale modifier
            pokemonData.setScaleModifier(pokemonData.getScaleModifier() * scaleFactor);

            // Re-apply Pokémon data into entity so size updates
            entity.setPokemon(pokemonData);
        }

        entity.setAiDisabled(true);
        // Spawn into world
        serverWorld.spawnEntity(entity);
    }

    public static void clearPokemonAtSpawner(ServerWorld world, BlockPos spawnerPos) {
        // Define search area around the spawner
        Box area = new Box(spawnerPos).expand(1);

        for (Entity entity : world.getOtherEntities(null, area)) {
            if (entity instanceof LivingEntity living) {
                Identifier id = Registries.ENTITY_TYPE.getId(living.getType());
                if (id != null && "cobblemon".equals(id.getNamespace())) {
                    entity.kill(); // removes Pokémon without needing the API
                }
            }
        }
    }
    public static String pickPokemon(boolean allowLegendary) {
        Collection<Species> allSpecies = PokemonSpecies.INSTANCE.getSpecies();

        List<Species> filtered = allSpecies.stream()
                .filter(species -> {
                    if (allowLegendary) {
                        return true; // Allow all if legendary is enabled
                    }

                    // Check multiple label possibilities
                    Set<String> labels = species.getLabels();
                    return !labels.contains("legendary")
                            && !labels.contains("mythical")
                            && !labels.contains("ultra_beast")
                            && !labels.contains("paradox");
                })
                .collect(Collectors.toList());

        // Debug log - remove after testing
        if (filtered.isEmpty()) {
            System.out.println("WARNING: No Pokemon match criteria! Total available: " + allSpecies.size());
            // Print first few species and their labels for debugging
            allSpecies.stream().limit(5).forEach(s ->
                    System.out.println(s.getName() + " labels: " + s.getLabels())
            );
        }

        if (filtered.isEmpty()) {
            return "Pikachu"; // Fallback
        }
        Random random =  new Random();
        return filtered.get(random.nextInt(filtered.size())).getName();
    }

}
