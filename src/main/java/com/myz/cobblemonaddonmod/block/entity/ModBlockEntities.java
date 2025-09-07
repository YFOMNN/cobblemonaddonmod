package com.myz.cobblemonaddonmod.block.entity;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.block.ModBlocks;
import com.myz.cobblemonaddonmod.block.entity.custom.GrillBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
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
            );    public static void registerBlockEntities()
    {
        CobblemonAddonMod.LOGGER.info("Registering Block Entries for " +  CobblemonAddonMod.MOD_ID);
    }
}

