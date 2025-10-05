package com.myz.cobblemonaddonmod;

import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.enchantment.ModEnchantmentEffects;
import com.myz.cobblemonaddonmod.item.ModItems;
import com.myz.cobblemonaddonmod.networking.ModNetworking;
import com.myz.cobblemonaddonmod.screen.ModScreenHandlers;
import net.fabricmc.api.ModInitializer;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		ModEnchantmentEffects.registerEnchantmentEffects();
		ModScreenHandlers.registerScreenHandlers(); // This line is the fix
		ModNetworking.registerC2SPackets();

		//registerPackets();
	}
}