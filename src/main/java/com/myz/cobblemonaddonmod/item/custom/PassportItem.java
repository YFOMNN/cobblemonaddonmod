    package com.myz.cobblemonaddonmod.item.custom;

    import com.cobblemon.mod.common.Cobblemon;
    import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
    import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
    import com.cobblemon.mod.common.pokemon.FormData;
    import com.cobblemon.mod.common.pokemon.Pokemon;
    import net.minecraft.entity.LivingEntity;
    import net.minecraft.entity.player.PlayerEntity;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemStack;
    import net.minecraft.nbt.NbtCompound;
    import net.minecraft.server.command.ServerCommandSource;
    import net.minecraft.server.network.ServerPlayerEntity;
    import net.minecraft.server.world.ServerWorld;
    import net.minecraft.text.Text;
    import net.minecraft.util.ActionResult;
    import net.minecraft.util.Hand;
    import net.minecraft.util.TypedActionResult;
    import net.minecraft.util.math.Vec3d;
    import net.minecraft.world.World;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Objects;
    import java.util.Random;

    public class PassportItem extends Item {
        private static final Random RANDOM = new Random();

        public PassportItem(Settings settings) {
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
                if(party.get(0) != null)
                     pokemon= party.get(0);
                else {
                    return TypedActionResult.success(stack, world.isClient());
                }

                // Get all available forms for this species
                List<FormData> allForms = new ArrayList<>();
                List<String> regionalForms = List.of("hisui", "galar", "alola", "paldea");

                for (FormData form : pokemon.getSpecies().getForms()) {
                    String formName = form.getName().toLowerCase();
                    System.out.println(formName);
                    for (String regional : regionalForms) {
                        if (formName.contains(regional)) {
                            allForms.add(form);
                            break;
                        }
                    }
                }

                // Remove the current form from the list
                String currentForm = pokemon.getForm().getName();
                allForms.removeIf(form -> form.getName().equals(currentForm));

                // Check if there are other forms available
                if (allForms.isEmpty()) {
                    user.sendMessage(Text.literal(pokemon.getSpecies().getName() +
                            " has no alternate forms!"), false);
                    return TypedActionResult.success(stack, world.isClient());
                }

                // Pick a random form
                int pickedForm = RANDOM.nextInt(allForms.size());
                FormData newForm = allForms.get(pickedForm);

                ServerCommandSource source = Objects.requireNonNull(user.getServer()).getCommandSource()
                        .withLevel(4) // Full permissions
                        .withPosition(Vec3d.ofCenter(user.getBlockPos()).add(0,0.5f,0)); // Use the spawn position as command "location"

                // Build the command string
                String regionalFormText = "";
                System.out.println(newForm.getName());

                if(newForm.getName().toLowerCase().contains("hisui"))
                {
                    regionalFormText = "hisuian";
                }
                else if(newForm.getName().toLowerCase().contains("galaria"))
                {
                    regionalFormText = "galarian";
                }
                else if(newForm.getName().toLowerCase().contains("alola"))
                {
                    regionalFormText = "alolan";
                }
                else if(newForm.getName().toLowerCase().contains("paldea"))
                {
                    regionalFormText = "paldean";
                }
                String command = "pokeeditother " + user.getName().getString() + " 1 form= " + regionalFormText;
                System.out.println(command);
                // Execute the command as the server
                user.getServer().getCommandManager().executeWithPrefix(source, command);
                // Send feedback
                user.sendMessage(Text.literal("Changed " + pokemon.getSpecies().getName() +
                        " in slot " + (1) + " to " + regionalFormText + " form!"), false);

                // Consume the item
                stack.decrement(1);
            }
            return TypedActionResult.success(stack, world.isClient());

        }
    }