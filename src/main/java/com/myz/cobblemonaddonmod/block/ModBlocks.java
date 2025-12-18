package com.myz.cobblemonaddonmod.block;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.block.custom.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block FOOD_GRILL = registerBlock("food_grill",new GrillBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.ANVIL)));
    public static final Block POKEMON_SPAWNWER_BLOCK = registerBlock("pokemon_spawner_block",new PokemonSpawnerBlock(AbstractBlock.Settings.create().strength(2f).requiresTool().sounds(BlockSoundGroup.WOOD)));
    public static final Block DATA_RECEIVER = registerBlock("data_receiver_block",new DataReceiverBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.ANVIL)));
    public static final Block GUESS_THE_COBBLEMON_CONTROL_BLOCK = registerBlock("guess_the_cobblemon_control_block",new GuessTheCobblemonControlBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.COPPER)));
    public static final Block RANDOM_POKEMON_BATTLE_BLOCK = registerBlock("random_pokemon_battle_block",new RandomPokemonBattleBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.COPPER)));
    public static final Block EXPERIENCE_BLOCK = registerBlock("experience_block",new ExperienceBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.COPPER)));
    public static final Block GAME_FLOOR_BLOCK = registerBlock("game_floor_block",new GameFloorBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.COPPER)));
    public static final Block SUPPLY_CRATE_BLOCK = registerBlock("supply_crate_block",new SupplyCrateBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.COPPER)));


    private static Block registerBlock(String name, Block block)
    {
        registerBlockItem(name,block);
        return Registry.register(Registries.BLOCK, Identifier.of(CobblemonAddonMod.MOD_ID,name),block);
    }

    private static void registerBlockItem(String name, Block block){
        Registry.register(Registries.ITEM, Identifier.of(CobblemonAddonMod.MOD_ID,name), new BlockItem(block,new Item.Settings()));
    }

    public static void registerModBlocks()
    {
        CobblemonAddonMod.LOGGER.info("Registering mod blocks for " + CobblemonAddonMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(fabricItemGroupEntries ->
        {
            fabricItemGroupEntries.add(ModBlocks.FOOD_GRILL);
            fabricItemGroupEntries.add(ModBlocks.POKEMON_SPAWNWER_BLOCK);
            fabricItemGroupEntries.add(ModBlocks.DATA_RECEIVER);
            fabricItemGroupEntries.add(ModBlocks.GUESS_THE_COBBLEMON_CONTROL_BLOCK);
            fabricItemGroupEntries.add(ModBlocks.RANDOM_POKEMON_BATTLE_BLOCK);
            fabricItemGroupEntries.add(ModBlocks.EXPERIENCE_BLOCK);
            fabricItemGroupEntries.add(ModBlocks.GAME_FLOOR_BLOCK);
            fabricItemGroupEntries.add(ModBlocks.SUPPLY_CRATE_BLOCK);
        });
    }
}
