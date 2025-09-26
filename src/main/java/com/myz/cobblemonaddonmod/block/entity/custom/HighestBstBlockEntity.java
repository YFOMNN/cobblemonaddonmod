package com.myz.cobblemonaddonmod.block.entity.custom;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.PokemonStoreManager;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.cobblemon.mod.common.api.storage.pc.PCStore;
import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.screen.custom.HighestBSTScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import com.cobblemon.mod.common.api.storage.PokemonStoreManager;
import com.cobblemon.mod.common.api.storage.StorePosition;
import com.cobblemon.mod.common.api.storage.party.PartyStore;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;


import java.util.*;


public class HighestBstBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    private Set<UUID> joinedPlayers = new HashSet<>();
    private boolean gameActive = false;


    public HighestBstBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HIGHEST_BST_BE, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("screen.highestbstmod.highest_bst_block");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HighestBSTScreenHandler(syncId, playerInventory, this);
    }

    public void addPlayer(PlayerEntity player) {
        if (!gameActive) {
            joinedPlayers.add(player.getUuid());
            markDirtyAndSync();
        }
    }

    public void removePlayer(PlayerEntity player) {
        if (!gameActive) {
            joinedPlayers.remove(player.getUuid());
            markDirtyAndSync();
        }
    }
    public boolean isPlayerJoined(PlayerEntity player) {
        return joinedPlayers.contains(player.getUuid());
    }

    public Set<UUID> getJoinedPlayers() {
        return new HashSet<>(joinedPlayers);
    }

    public void startGame() {
        // Use getWorld() and check if it's null and on the server side
        World world = this.getWorld();
        if (world == null || world.isClient) {
            return;
        }

        if (!gameActive && !joinedPlayers.isEmpty()) {
            gameActive = true;
            System.out.println("Game started with players: " + joinedPlayers.size());

            // Safely get the server and player manager
            MinecraftServer server = world.getServer();
            if (server == null) {
                return; // Can't proceed without the server
            }
            PlayerManager playerManager = server.getPlayerManager();

            for (UUID playerUuid : joinedPlayers) {
                ServerPlayerEntity playerEntity = playerManager.getPlayer(playerUuid);

                if (playerEntity != null) {
                    PlayerPartyStore party = Cobblemon.INSTANCE.getStorage().getParty(playerEntity);
                    PCStore pc = Cobblemon.INSTANCE.getStorage().getPC(playerEntity);

                    List<Pokemon> pokemonToMove = new ArrayList<>(party.toGappyList());

                    for (Pokemon pokemon : pokemonToMove) {
                        if (pc.add(pokemon)) {
                            party.remove(pokemon);
                        } else {
                            playerEntity.sendMessage(Text.literal("Your PC is full! Not all Pokémon could be moved."), false);
                            break;
                        }
                    }
                    playerEntity.sendMessage(Text.literal("Your party Pokémon have been moved to the PC!"), false);
                }
            }
            System.out.println("Game started with players: " + joinedPlayers.size());
            markDirtyAndSync();
        }
    }

    public void stopGame() {
        if (gameActive) {
            gameActive = false;
            System.out.println("Game stopped. Final players: " + joinedPlayers);
            joinedPlayers.clear();
            markDirtyAndSync();
        }
    }

    public boolean isGameActive() {
        return gameActive;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    private void markDirtyAndSync() {
        markDirty();
        // Use getWorld() here as well
        World world = this.getWorld();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public Object getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return pos;
    }
}