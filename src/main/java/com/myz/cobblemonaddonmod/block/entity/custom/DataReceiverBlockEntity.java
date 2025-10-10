package com.myz.cobblemonaddonmod.block.entity.custom;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.myz.cobblemonaddonmod.CobblemonAddonMod;
import com.myz.cobblemonaddonmod.PokemonSpawnHelper;
import com.myz.cobblemonaddonmod.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DataReceiverBlockEntity extends BlockEntity {
    public  List<BlockPos> spawnPositions = new ArrayList<>();
    private PokemonSpawnerBlockEntity lastFlippedPokemonBlock;
    private boolean isPowered;
    private int numberOfPairsFound;
    private int numberOfPairs;

    public String getSelectedPokemon() {
        return selectedPokemon;
    }

    public void setSelectedPokemon(String selectedPokemon) {
        this.selectedPokemon = selectedPokemon;
    }

    private String selectedPokemon;

    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        isPowered = powered;
        markDirty(); // marks BE as needing save
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public DataReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DATA_RECEIVER_EN, pos, state);
    }
    public void updateFlippedPokemon(BlockEntity be, World world)
    {

        if (be == lastFlippedPokemonBlock) {
            CobblemonAddonMod.LOGGER.info("Clicked the same block twice. Ignoring.");
            return;
        }

        if (lastFlippedPokemonBlock == null)
        {
            lastFlippedPokemonBlock = (PokemonSpawnerBlockEntity) be ;
            if(lastFlippedPokemonBlock.isActive())
            {
                lastFlippedPokemonBlock = null;
                return;
            }
            PokemonSpawnHelper.spawnPokemonAt(Objects.requireNonNull(world), lastFlippedPokemonBlock.getPos(), lastFlippedPokemonBlock.getPokemonOnBlock(),"uncatchable");
            ((PokemonSpawnerBlockEntity) be).setActive(true);
        }
        else
        {

            PokemonSpawnerBlockEntity pokemonSpawnerBlockEntity = (PokemonSpawnerBlockEntity) be;
            if(lastFlippedPokemonBlock.isActive() && ((PokemonSpawnerBlockEntity) be).isActive())
                return;
            if(pokemonSpawnerBlockEntity.getPokemonOnBlock().equals(lastFlippedPokemonBlock.getPokemonOnBlock()))
            {
                PokemonSpawnHelper.spawnPokemonAt(Objects.requireNonNull(world), be.getPos(), ((PokemonSpawnerBlockEntity) be).getPokemonOnBlock(),"uncatchable");
                CobblemonAddonMod.LOGGER.info("Correct");
                numberOfPairsFound++;
                if(numberOfPairsFound == numberOfPairs){
                    PokemonSpawnHelper.spawnCatchablePokemonAt(Objects.requireNonNull(world.getServer()), this.getPos(), lastFlippedPokemonBlock.getPokemonOnBlock());
                }
                ((PokemonSpawnerBlockEntity) be).setActive(true);
                lastFlippedPokemonBlock.setActive(true);

            }
            else
            {
                PokemonSpawnHelper.spawnPokemonAt(Objects.requireNonNull(world), be.getPos(), ((PokemonSpawnerBlockEntity) be).getPokemonOnBlock(),"uncatchable");
                CobblemonAddonMod.LOGGER.info("Wrong");
                if (world instanceof ServerWorld serverWorld) {
                    PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, lastFlippedPokemonBlock.getPos());
                    PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, be.getPos());
                }
                ((PokemonSpawnerBlockEntity) be).setActive(false);
                lastFlippedPokemonBlock.setActive(false);
            }
            lastFlippedPokemonBlock = null;
        }
    }
    public void getSideSpawnPoints(BlockState state, World world, BlockPos pos, PlayerEntity player)
    {
        if (!world.isClient()) {
            clearSpawnPoints();
            spawnPositions.clear();
            if (world instanceof ServerWorld serverWorld)
                PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, this.getPos());
            Direction facing = state.get(Properties.HORIZONTAL_FACING);

// Define length & width
            int length = 24; // forward
            int halfWidth = 24; // left/right from center

            for (int forward = 1; forward <= length; forward++) {
                for (int side = -halfWidth; side <= halfWidth; side++) {
                    BlockPos checkPos;

                    if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                        // forward = Z axis, side = X axis
                        checkPos = pos.add(side, 0, facing.getOffsetZ() * forward);
                    } else {
                        // forward = X axis, side = Z axis
                        checkPos = pos.add(facing.getOffsetX() * forward, 0, side);
                    }

                    BlockEntity be = world.getBlockEntity(checkPos);
                    if (be instanceof PokemonSpawnerBlockEntity scanner) {
                        spawnPositions.add(checkPos);
                        scanner.setPokemonOnBlock(null);
                        scanner.setDataReceiverBlockEntity(this);
                        scanner.setGameMode(0);
                    }
                }
            }

            player.sendMessage(
                    Text.literal("Receiver collected " + spawnPositions.size() + " nearby scanners."),
                    false
            );
        }
    }
    public void clearSpawnPoints()
    {
        if (!world.isClient()) {
            lastFlippedPokemonBlock = null;
            for(BlockPos spawnPos: spawnPositions)
            {
                BlockEntity be = world.getBlockEntity(spawnPos);
                if (be instanceof PokemonSpawnerBlockEntity scanner) {
                    scanner.setPokemonOnBlock(null);
                    scanner.setDataReceiverBlockEntity(null);
                    scanner.setActive(false);
                    selectedPokemon = null;
                    if (world instanceof ServerWorld serverWorld) {
                        PokemonSpawnHelper.clearPokemonAtSpawner(serverWorld, spawnPos);
                    }
                }
            }
        }
    }

    public void prepareForMemoryGame() {
        if (!world.isClient()) {
            numberOfPairs = spawnPositions.size() / 2;
            numberOfPairsFound = 0;
            String selectedPokemon;
            for (int i = 0;i<numberOfPairs;i++)
            {
                selectedPokemon = (PokemonSpawnHelper.pickPokemon(false));
                Random random =  new Random();
                while(true)
                {
                    BlockEntity be = world.getBlockEntity(spawnPositions.get(random.nextInt(spawnPositions.size())));
                    if (be instanceof PokemonSpawnerBlockEntity scanner)
                    {
                        if(scanner.getPokemonOnBlock() == null)
                        {
                            scanner.setPokemonOnBlock(selectedPokemon);
                            CobblemonAddonMod.LOGGER.info("Set pokemon " + scanner.getPokemonOnBlock());


                            break;
                        }
                    }
                }
                while(true)
                {
                    BlockEntity be = world.getBlockEntity(spawnPositions.get(random.nextInt(spawnPositions.size())));
                    if (be instanceof PokemonSpawnerBlockEntity scanner)
                    {
                        if(scanner.getPokemonOnBlock() == null)
                        {
                            scanner.setPokemonOnBlock(selectedPokemon);
                            CobblemonAddonMod.LOGGER.info("Set pokemon " + scanner.getPokemonOnBlock());
                            break;
                        }
                    }
                }


            }

            for(BlockPos spawnPos: spawnPositions)
            {
                BlockEntity be = world.getBlockEntity(spawnPos);
                if (be instanceof PokemonSpawnerBlockEntity scanner) {
                    scanner.setGameMode(1);
                }
            }
        }
    }
    @Override
    protected void writeNbt(NbtCompound nbt,RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        // Save your custom data
        nbt.putBoolean("IsPowered", isPowered);
        nbt.putInt("PairsFound", numberOfPairsFound);
        nbt.putInt("Pairs", numberOfPairs);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt,registryLookup);

        // Load your custom data
        if (nbt.contains("IsPowered")) {
            isPowered = nbt.getBoolean("IsPowered");
        }
        if (nbt.contains("PairsFound")) {
            numberOfPairsFound = nbt.getInt("PairsFound");
        }
        if (nbt.contains("Pairs")) {
            numberOfPairs = nbt.getInt("Pairs");
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
