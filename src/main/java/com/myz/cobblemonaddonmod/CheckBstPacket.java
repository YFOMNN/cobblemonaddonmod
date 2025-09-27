package com.myz.cobblemonaddonmod;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record CheckBstPacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<CheckBstPacket> ID = new CustomPayload.Id<>(Identifier.of(CobblemonAddonMod.MOD_ID, "check_bst"));
    public static final PacketCodec<PacketByteBuf, CheckBstPacket> CODEC = PacketCodec.of(CheckBstPacket::write, CheckBstPacket::new);

    public CheckBstPacket(PacketByteBuf buf) { this(buf.readBlockPos()); }
    public void write(PacketByteBuf buf) { buf.writeBlockPos(this.pos); }
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}