package com.myz.cobblemonaddonmod.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Hand;

public class ModClientNetworking {

    // Client-side method to send the packet
    public static void sendTeleportRequest(String targetName, Hand hand) {
        ClientPlayNetworking.send(new ModNetworking.TeleportRequestPayload(targetName, hand));
    }
}