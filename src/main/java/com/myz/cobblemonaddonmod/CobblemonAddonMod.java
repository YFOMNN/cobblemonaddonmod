package com.myz.cobblemonaddonmod;

import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.item.ModItems;
import com.myz.cobblemonaddonmod.screen.ModScreenHandler;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonAddonMod implements ModInitializer {
	public static final String MOD_ID = "cobblemonaddonmod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandler.registerScreenHandlers();
	}
}