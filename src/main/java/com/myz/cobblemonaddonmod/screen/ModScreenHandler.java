package com.myz.cobblemonaddonmod.screen;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.block.entity.custom.HighestBstBlockEntity;
import com.myz.cobblemonaddonmod.screen.custom.HighestBSTScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
public class ModScreenHandler {

    public static ScreenHandlerType<HighestBSTScreenHandler> HIGHEST_BST_SCREEN_HANDLER_SCREEN_HANDLER = Registry.register(
            Registries.SCREEN_HANDLER,
            Identifier.of(CobblemonAddonMod.MOD_ID, "highest_bst_screen_handler"),
            // This now correctly calls our new client-side constructor
            new ExtendedScreenHandlerType<>(
                    HighestBSTScreenHandler::new,
                    BlockPos.PACKET_CODEC
            )
    );
    public static void registerScreenHandlers()
    {
        CobblemonAddonMod.LOGGER.info("Registering UI");

    }
}