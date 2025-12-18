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

    // Register on both sides
    public static void registerC2SPackets() {
        // Register payload type for both client and server
        PayloadTypeRegistry.playC2S().register(TeleportRequestPayload.ID, TeleportRequestPayload.CODEC);

        // Server receiver
        ServerPlayNetworking.registerGlobalReceiver(TeleportRequestPayload.ID, (payload, context) -> {
            // Validate payload before executing
            if (payload == null || payload.targetName() == null || payload.hand() == null) {
                CobblemonAddonMod.LOGGER.error("Received invalid teleport request payload");
                return;
            }

            context.server().execute(() -> {
                try {
                    TeleportTargetScreenHandler.teleportToTarget(
                            context.player(),
                            payload.targetName(),
                            payload.hand()
                    );
                } catch (Exception e) {
                    CobblemonAddonMod.LOGGER.error("Error handling teleport request", e);
                }
            });
        });
    }

    public record TeleportRequestPayload(String targetName, Hand hand) implements CustomPayload {
        public static final CustomPayload.Id<TeleportRequestPayload> ID = new CustomPayload.Id<>(TELEPORT_REQUEST_ID);

        // Simplified codec without xmap to avoid potential issues
        public static final PacketCodec<RegistryByteBuf, TeleportRequestPayload> CODEC = new PacketCodec<>() {
            @Override
            public TeleportRequestPayload decode(RegistryByteBuf buf) {
                String targetName = PacketCodecs.STRING.decode(buf);
                int handOrdinal = PacketCodecs.VAR_INT.decode(buf);
                Hand hand = handOrdinal == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND;
                return new TeleportRequestPayload(targetName, hand);
            }

            @Override
            public void encode(RegistryByteBuf buf, TeleportRequestPayload payload) {
                PacketCodecs.STRING.encode(buf, payload.targetName());
                PacketCodecs.VAR_INT.encode(buf, payload.hand() == Hand.MAIN_HAND ? 0 : 1);
            }
        };

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}