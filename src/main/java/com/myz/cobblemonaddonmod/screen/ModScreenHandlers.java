package com.myz.cobblemonaddonmod.screen;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.screen.custom.TeleportTargetScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {

    public static ScreenHandlerType<TeleportTargetScreenHandler> TELEPORT_TARGET_SCREEN_HANDLER;

    public static void registerScreenHandlers() {
        TELEPORT_TARGET_SCREEN_HANDLER =
                Registry.register(
                        Registries.SCREEN_HANDLER,
                        Identifier.of(CobblemonAddonMod.MOD_ID, "teleport_target"),
                        new ScreenHandlerType<>((syncId, inventory) ->
                                new TeleportTargetScreenHandler(syncId, inventory, net.minecraft.util.Hand.MAIN_HAND), null)
                );
    }
}