package com.myz.cobblemonaddonmod;

import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record JoinLeaveGamePacket(BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<JoinLeaveGamePacket> ID = new CustomPayload.Id<>(Identifier.of(CobblemonAddonMod.MOD_ID, "join_leave_game"));
    public static final PacketCodec<PacketByteBuf, JoinLeaveGamePacket> CODEC = PacketCodec.of(JoinLeaveGamePacket::write, JoinLeaveGamePacket::new);

    public JoinLeaveGamePacket(PacketByteBuf buf) { this(buf.readBlockPos()); }
    public void write(PacketByteBuf buf) { buf.writeBlockPos(this.pos); }
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}