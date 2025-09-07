package com.myz.cobblemonaddonmod.block;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.block.custom.GrillBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block FOOD_GRILL = registerBlock("food_grill",new GrillBlock(AbstractBlock.Settings.create().strength(4f).requiresTool().sounds(BlockSoundGroup.ANVIL)));

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
        });
    }
}
