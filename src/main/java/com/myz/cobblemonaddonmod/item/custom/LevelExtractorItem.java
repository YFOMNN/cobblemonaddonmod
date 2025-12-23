    package com.myz.cobblemonaddonmod.item.custom;

    import com.cobblemon.mod.common.Cobblemon;
    import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
    import com.cobblemon.mod.common.pokemon.FormData;
    import com.cobblemon.mod.common.pokemon.Pokemon;
    import net.minecraft.entity.player.PlayerEntity;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.server.command.ServerCommandSource;
    import net.minecraft.server.network.ServerPlayerEntity;
    import net.minecraft.text.Text;
    import net.minecraft.util.Hand;
    import net.minecraft.util.TypedActionResult;
    import net.minecraft.util.math.Vec3d;
    import net.minecraft.world.World;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;
    import java.util.Random;

    public class LevelExtractorItem extends Item {
        private static final Random RANDOM = new Random();

        public LevelExtractorItem(Settings settings) {
            super(settings);
        }

        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            super.use(world, user, hand);
            ItemStack stack = user.getStackInHand(hand);
            if (!user.getWorld().isClient && user instanceof ServerPlayerEntity serverPlayer) {



                // Get the player's party
                PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(serverPlayer);
                Pokemon pokemon;

                if(party.get(0) != null){
                    pokemon= party.get(0);
                    ServerCommandSource source = Objects.requireNonNull(user.getServer()).getCommandSource()
                            .withLevel(4) // Full permissions
                            .withPosition(Vec3d.ofCenter(user.getBlockPos()).add(0,0.5f,0)); // Use the spawn position as command "location"

                    // Build the command string
                    String command = "pokeeditother " + user.getName().getString() + " 1 level=" + (pokemon.getLevel()-2);
                    System.out.println(command);
                    // Execute the command as the server
                    user.getServer().getCommandManager().executeWithPrefix(source, command);
                    // Send feedback
                    user.sendMessage(Text.literal("Removed 10 levels from " + pokemon.getSpecies().getName()), false);

                    // Consume the item
                    stack.decrement(1);
                }
                else {
                    return TypedActionResult.success(stack, world.isClient());
                }
            }
            return TypedActionResult.success(stack, world.isClient());

        }
    }