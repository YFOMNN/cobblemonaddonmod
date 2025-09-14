package com.myz.cobblemonaddonmod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class PokemonSpawnHelper {
    public static void spawnPokemonAt(MinecraftServer server, BlockPos pos, String pokemonName) {
        // Create a command source with OP level
        ServerCommandSource source = server.getCommandSource()
                .withLevel(4) // Full permissions
                .withPosition(Vec3d.ofCenter(pos).add(0,0.5f,0)); // Use the spawn position as command "location"

        // Build the command string
        String command = "spawnpokemon " + pokemonName + " no_ai";

        // Execute the command as the server
        server.getCommandManager().executeWithPrefix(source, command);
    }
    public static void clearPokemonAtSpawner(ServerWorld world, BlockPos spawnerPos) {
        // Define search area around the spawner
        Box area = new Box(spawnerPos).expand(2);

        for (Entity entity : world.getOtherEntities(null, area)) {
            if (entity instanceof LivingEntity living) {
                Identifier id = Registries.ENTITY_TYPE.getId(living.getType());
                if (id != null && "cobblemon".equals(id.getNamespace())) {
                    entity.kill(); // removes Pokémon without needing the API
                }
            }
        }
    }
}
