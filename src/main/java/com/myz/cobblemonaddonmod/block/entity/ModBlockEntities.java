package com.myz.cobblemonaddonmod.block.entity;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.custom.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static final BlockEntityType<GrillBlockEntity> GRILL_RE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(CobblemonAddonMod.MOD_ID, "grill_be"),
                    // CORRECTED LINE BELOW:
                    FabricBlockEntityTypeBuilder.create(GrillBlockEntity::new, ModBlocks.FOOD_GRILL).build(null) // Keep build(null) for Fabric's builder
            );
    public static final BlockEntityType<PokemonSpawnerBlockEntity> POKEMON_SPAWN_EN =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(CobblemonAddonMod.MOD_ID, "pokemon_en"),
                    // CORRECTED LINE BELOW:
                    FabricBlockEntityTypeBuilder.create(PokemonSpawnerBlockEntity::new, ModBlocks.POKEMON_SPAWNWER_BLOCK).build(null) // Keep build(null) for Fabric's builder
            );
    public static final BlockEntityType<DataReceiverBlockEntity> DATA_RECEIVER_EN =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(CobblemonAddonMod.MOD_ID, "data_receiver_en"),
                    // CORRECTED LINE BELOW:
                    FabricBlockEntityTypeBuilder.create(DataReceiverBlockEntity::new, ModBlocks.DATA_RECEIVER).build(null) // Keep build(null) for Fabric's builder
            );
    public static final BlockEntityType<ExperienceBlockEntity> EXPERIENCE_BLOCK_EN =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(CobblemonAddonMod.MOD_ID, "experience_block_en"),
                    // CORRECTED LINE BELOW:
                    FabricBlockEntityTypeBuilder.create(ExperienceBlockEntity::new, ModBlocks.EXPERIENCE_BLOCK).build(null) // Keep build(null) for Fabric's builder
            );
    public static final BlockEntityType<SupplyCrateBlockEntity> SUPPLY_CRATE_BLOCK_EN =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(CobblemonAddonMod.MOD_ID, "suppy_crate_block_en"),
                    // CORRECTED LINE BELOW:
                    FabricBlockEntityTypeBuilder.create(SupplyCrateBlockEntity::new, ModBlocks.SUPPLY_CRATE_BLOCK).build(null) // Keep build(null) for Fabric's builder
            );
    public static void registerBlockEntities()
    {
        CobblemonAddonMod.LOGGER.info("Registering Block Entries for " +  CobblemonAddonMod.MOD_ID);
    }


}

