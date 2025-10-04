package com.myz.cobblemonaddonmod.networking;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.screen.custom.TeleportTargetScreenHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class ModNetworking {

    public static final Identifier TELEPORT_REQUEST_ID = Identifier.of(CobblemonAddonMod.MOD_ID, "teleport_request");

    // Server-side registration only
    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(TeleportRequestPayload.ID, TeleportRequestPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TeleportRequestPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                TeleportTargetScreenHandler.teleportToTarget(
                        context.player(),
                        payload.targetName(),
                        payload.hand()
                );
            });
        });
    }

    public record TeleportRequestPayload(String targetName, Hand hand) implements CustomPayload {
        public static final CustomPayload.Id<TeleportRequestPayload> ID = new CustomPayload.Id<>(TELEPORT_REQUEST_ID);

        public static final PacketCodec<RegistryByteBuf, TeleportRequestPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.STRING, TeleportRequestPayload::targetName,
                PacketCodecs.VAR_INT.xmap(
                        i -> i == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND,
                        h -> h == Hand.MAIN_HAND ? 0 : 1
                ),
                TeleportRequestPayload::hand,
                TeleportRequestPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}