package com.myz.cobblemonaddonmod.block.entity.custom;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.screen.custom.HighestBSTScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HighestBstBlockEntity extends BlockEntity  implements ExtendedScreenHandlerFactory { // No longer needs screen handler factory
    private final Set<UUID> joinedPlayers = new HashSet<>();

    public HighestBstBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HIGHEST_BST_BE, pos, state);
    }
    // --- THIS IS THE NEW, CLEAN METHOD FROM YOUR SUGGESTION ---
    public void handlePlayerJoinAttempt(ServerPlayerEntity player) {
        // We need the world to sync changes, so we get it from the player.
        World world = player.getWorld();

        if (this.isPlayerJoined(player)) {
            // Player is already in, so they must be leaving.
            this.removePlayer(player);
            player.sendMessage(Text.literal("You have left the competition."), false);
        } else {
            // Player is trying to join. Perform the server-side check.
                this.addPlayer(player);
        }

        // CRITICAL: After any change, sync the block entity with all clients.
        this.markDirtyAndSync(world);
    }
// --- END OF NEW METHOD ---


    // Your existing Player Management methods are still needed, but they become simpler.
    public void addPlayer(PlayerEntity player) { joinedPlayers.add(player.getUuid()); }
    public void removePlayer(PlayerEntity player) { joinedPlayers.remove(player.getUuid()); }
    public boolean isPlayerJoined(PlayerEntity player) { return joinedPlayers.contains(player.getUuid()); }public Set<UUID> getJoinedPlayers() { return new HashSet<>(joinedPlayers); }

    // --- Core Logic ---
    public void findAndAnnounceWinner(World world) {
        if (world.isClient() || joinedPlayers.isEmpty()) {
            return;
        }

        MinecraftServer server = world.getServer();
        if (server == null) return;

        ServerPlayerEntity overallWinner = null;
        long highestTotalBst = -1L; // Use a long to prevent overflow with a full party of high BST mons

        List<ServerPlayerEntity> onlinePlayers = new ArrayList<>();

        // Loop through every player who has joined the competition.
        for (UUID playerUuid : joinedPlayers) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
            if (player != null) {
                onlinePlayers.add(player);
                PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(player);

                // --- THIS IS THE NEW, SIMPLIFIED LOGIC ---
                long currentPartyTotalBst = 0;

                // Iterate through every Pokémon in the party and add its BST to the total.
                for (Pokemon pokemon : party) { // This correctly iterates through non-null Pokémon.
                    Map<Stat,Integer> baseStats = pokemon.getForm().getBaseStats();
                    int currentBaseTotal = baseStats.get(Stats.HP)
                            + baseStats.get(Stats.ATTACK)
                            + baseStats.get(Stats.DEFENCE)
                            + baseStats.get(Stats.SPECIAL_ATTACK)
                            + baseStats.get(Stats.SPECIAL_DEFENCE)
                            + baseStats.get(Stats.SPEED);
                    currentPartyTotalBst += currentBaseTotal;

                    Text message = pokemon.getDisplayName()                 // This is already a translatable Text object
                            .copy()                                      // Create a mutable copy to append to
                            .append(Text.literal( " BST: " + currentBaseTotal)); // Append the rest as a literal Text object

                    for (ServerPlayerEntity curplayer : onlinePlayers) {
                        curplayer.sendMessage(player.getDisplayName());
                        curplayer.sendMessage(message, false);
                    }
                }

                // Compare this player's total party BST to the best found so far.
                if (currentPartyTotalBst > highestTotalBst) {
                    highestTotalBst = currentPartyTotalBst;
                    overallWinner = player;
                }
                // --- END OF NEW LOGIC ---
            }
        }

        // Announce the result to all participants.
        if (overallWinner != null) {
            Text message = Text.literal("Competition Result: ")
                    .append(overallWinner.getDisplayName())
                    .append(" wins with a Total Party BST of " + highestTotalBst + "!");

            for (ServerPlayerEntity player : onlinePlayers) {
                player.sendMessage(message, false);
            }
        } else {
            Text message = Text.literal("Could not determine a winner. Ensure all participants are online.");
            for (ServerPlayerEntity player : onlinePlayers) {
                player.sendMessage(message, false);
            }
        }

        // Reset for the next competition.
        joinedPlayers.clear();
        markDirtyAndSync(world);
    }
    // --- Synchronization ---
    public void markDirtyAndSync(World world) {
        if (world != null && !world.isClient()) {
            markDirty();
            world.updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        NbtList playerList = new NbtList();
        for (UUID playerUuid : this.joinedPlayers) {
            playerList.add(NbtString.of(playerUuid.toString()));
        }
        nbt.put("joinedPlayers", playerList);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.joinedPlayers.clear();
        NbtList playerList = nbt.getList("joinedPlayers", NbtElement.STRING_TYPE);
        for (NbtElement element : playerList) {
            this.joinedPlayers.add(UUID.fromString(element.asString()));
        }
    }

    @Nullable @Override public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }

    @Override
    public Text getDisplayName() {
        return Text.literal("Highest BST Challenge");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HighestBSTScreenHandler(syncId, playerInventory, this);
    }
    @Override
    public Object getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return pos;
    }
}