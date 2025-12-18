package com.myz.cobblemonaddonmod.networking;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Hand;

public class ModClientNetworking {

    // Client-side method to send the packet
    public static void sendTeleportRequest(String targetName, Hand hand) {
        // Validate before sending
        if (targetName == null || targetName.isEmpty()) {
            CobblemonAddonMod.LOGGER.error("Attempted to send teleport request with null/empty target name");
            return;
        }

        if (hand == null) {
            CobblemonAddonMod.LOGGER.error("Attempted to send teleport request with null hand");
            return;
        }

        // Check if we can send (connected to server)
        if (!ClientPlayNetworking.canSend(ModNetworking.TeleportRequestPayload.ID)) {
            CobblemonAddonMod.LOGGER.warn("Cannot send teleport request - not connected to server or server doesn't support this packet");
            return;
        }

        try {
            ClientPlayNetworking.send(new ModNetworking.TeleportRequestPayload(targetName, hand));
        } catch (Exception e) {
            CobblemonAddonMod.LOGGER.error("Error sending teleport request packet", e);
        }
    }
}