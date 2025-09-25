package com.myz.cobblemonaddonmod.block.entity.custom;

import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import com.myz.cobblemonaddonmod.screen.custom.HighestBSTScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HighestBstBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {

    public List<PlayerEntity> joinedPlayers =new ArrayList<>();

    private boolean gameStarted = false;

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean started) {
        this.gameStarted = started;
    }

    public HighestBstBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HIGHEST_BST_BE, pos, state);
    }
    @Override
    protected void writeNbt(NbtCompound nbt,RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt,registryLookup);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Highest BST Challenge");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HighestBSTScreenHandler(syncId,playerInventory,this.pos);
    }
}
