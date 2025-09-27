package com.myz.cobblemonaddonmod;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.storage.party.PlayerPartyStore;
import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.block.entity.custom.HighestBstBlockEntity;
import com.myz.cobblemonaddonmod.item.ModItems;
import com.myz.cobblemonaddonmod.screen.ModScreenHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.text.Text;

public class CobblemonAddonMod implements ModInitializer {
	public static final String MOD_ID = "cobblemonaddonmod";
	public static final Identifier JOIN_LEAVE_GAME_PACKET_ID = Identifier.of(MOD_ID, "join_leave_game");
	public static final Identifier START_STOP_GAME_PACKET_ID = Identifier.of(MOD_ID, "start_stop_game");// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandler.registerScreenHandlers();
		registerPackets();
	}
	private void registerPackets() {
		PayloadTypeRegistry.playC2S().register(JoinLeaveGamePacket.ID, JoinLeaveGamePacket.CODEC);
		PayloadTypeRegistry.playC2S().register(CheckBstPacket.ID, CheckBstPacket.CODEC);

		// --- HANDLER FOR JOINING/LEAVING ---
		ServerPlayNetworking.registerGlobalReceiver(JoinLeaveGamePacket.ID, (payload, context) -> {
			ServerPlayerEntity player = context.player();
			World world = player.getWorld();

			// We let the BlockEntity handle the logic, as it has a more stable state.
			// The server.execute wrapper is removed as it was not reliable enough.
			if (world.getBlockEntity(payload.pos()) instanceof HighestBstBlockEntity be) {
				be.handlePlayerJoinAttempt(player);
			}
		});

		// The Check BST handler is correct.
		ServerPlayNetworking.registerGlobalReceiver(CheckBstPacket.ID, (payload, context) -> {
			World world = context.player().getWorld();
			if (world.getBlockEntity(payload.pos()) instanceof HighestBstBlockEntity be) {
				be.findAndAnnounceWinner(world);
			}
		});
	}
}