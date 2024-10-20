package com.tompkins_development.autominers.block.entity;

import com.tompkins_development.autominers.block.ModBlocks;
import com.tompkins_development.autominers.gui.menu.AutoMinerMenu;
import com.tompkins_development.autominers.networking.payloads.BlockBreakPayload;
import com.tompkins_development.autominers.utils.ModTags;
import com.tompkins_development.autominers.utils.UpgradeCardType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO block breaks on fluid, implement if a block can be broken
// implement filtering, fortune, silk touch

public class AutoMinerBlockEntity extends BlockEntity implements MenuProvider {

    public static int INV_SIZE = 5;

    private int tickRate = 10;
    private int currentTick;

    private Map<Block, Integer> blockCounts;


    public final ItemStackHandler itemHandler = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) {
                return stack.is(ModTags.Items.DRILLS);
            } else {
                return stack.is(ModTags.Items.UPGRADE_CARDS);
            }
        }
    };

    private final ContainerData data;

    // Variables to keep track of the current position in the chunk
    private int currentY;
    private int currentX;
    private int currentZ;
    private boolean finishedChunk = false;

    private BlockPos nextBlockPos; // The next block to mine

    public AutoMinerBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.AUTO_MINER.get(), pos, blockState);
        this.blockCounts = new HashMap<>();

        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return 0;
            }

            @Override
            public void set(int i, int i1) {

            }

            @Override
            public int getCount() {
                return 0;
            }
        };

        // Initialize the current position variables
        this.currentY = -1; // Will be set properly during the first tick
        this.currentX = 0;
        this.currentZ = 0;
    }

    // Ticking
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide)
            clientTick(level, pos, state);
        else
            serverTick(level, pos, state);
    }

    private void clientTick(Level level, BlockPos pos, BlockState state) {
        // Client-side logic (if any)
    }

    private void serverTick(Level level, BlockPos pos, BlockState state) {
        if (finishedChunk) {
            return; // Chunk has been fully processed
        }

        if (meetsItemRequirements()) {
            if (nextBlockPos == null) {
                // Find the next block to mine
                scanForNextBlock(level);
            }

            if (nextBlockPos != null) {
                if (currentTick >= tickRate) {
                    // Time to mine the block
                    BlockState blockState = level.getBlockState(nextBlockPos);
                    if (canDestroy(blockState, nextBlockPos)) {
                        destroyBlock(level, nextBlockPos);
                        damageDrill();
                    }
                    currentTick = 0;
                    nextBlockPos = null; // Reset for the next block
                } else {
                    currentTick++;
                }
            }
        }
    }

    // Data

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("currentTick", currentTick);
        tag.putInt("currentX", currentX);
        tag.putInt("currentY", currentY);
        tag.putInt("currentZ", currentZ);
        tag.putBoolean("finishedChunk", finishedChunk);

        if (nextBlockPos != null) {
            tag.putIntArray("nextBlockPos", new int[]{nextBlockPos.getX(), nextBlockPos.getY(), nextBlockPos.getZ()});
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        currentTick = tag.getInt("currentTick");
        currentX = tag.getInt("currentX");
        currentY = tag.getInt("currentY");
        currentZ = tag.getInt("currentZ");
        finishedChunk = tag.getBoolean("finishedChunk");

        if (tag.contains("nextBlockPos")) {
            int[] posArray = tag.getIntArray("nextBlockPos");
            nextBlockPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
        } else {
            nextBlockPos = null;
        }
    }

    // Functionality

    private boolean meetsItemRequirements() {
        ItemStack itemStack = itemHandler.getStackInSlot(0);
        if (itemStack.isEmpty()) return false;
        if (!itemStack.is(ModTags.Items.DRILLS)) return false;
        return true;
    }

    private void damageDrill() {
        ItemStack itemStack = itemHandler.getStackInSlot(0);
        if (itemStack.isEmpty()) return;

        itemStack.hurtAndBreak(1, (ServerLevel) level, null, (p) -> System.out.println("Broken"));
    }

    private void scanForNextBlock(Level level) {
        if (currentY == -1) {
            // Initialize currentY on the first tick
            currentY = level.getMaxBuildHeight() - 1;
        }

        ChunkPos chunkPos = new ChunkPos(worldPosition);
        int minY = level.getMinBuildHeight();

        while (!finishedChunk && currentY >= minY) {
            BlockPos blockPos = new BlockPos(
                    chunkPos.getMinBlockX() + currentX,
                    currentY,
                    chunkPos.getMinBlockZ() + currentZ
            );
            BlockState blockState = level.getBlockState(blockPos);

            if (canDestroy(blockState, blockPos)) {
                nextBlockPos = blockPos;
                // Do not advance position here; we'll advance after mining
                return; // Found a block to mine
            }

            advancePosition(chunkPos, minY);
        }
    }

    private void advancePosition(ChunkPos chunkPos, int minY) {
        currentX++;

        if (currentX >= 16) {
            currentX = 0;
            currentZ++;

            if (currentZ >= 16) {
                currentZ = 0;
                currentY--;

                if (currentY < minY) {
                    finishedChunk = true; // Finished processing the chunk
                }
            }
        }
    }

    public void updateBlockCounts() {
        Map<Block, Integer> blockCountMap = new HashMap<>();

        if (finishedChunk) {
            this.blockCounts = blockCountMap; // Empty map
            return;
        }

        // Initialize currentY if it's uninitialized
        int startingY = this.currentY;
        if (startingY == -1) {
            startingY = level.getMaxBuildHeight() - 1;
        }

        // Loop through the chunk from currentY down to minY
        ChunkPos chunkPos = new ChunkPos(worldPosition);
        int minY = level.getMinBuildHeight();

        for (int y = startingY; y >= minY; y--) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos blockPos = new BlockPos(
                            chunkPos.getMinBlockX() + x,
                            y,
                            chunkPos.getMinBlockZ() + z
                    );
                    BlockState blockState = level.getBlockState(blockPos);

                    if (canDestroy(blockState, blockPos)) {
                        Block block = blockState.getBlock();

                        blockCountMap.merge(block, 1, Integer::sum);
                    }
                }
            }
        }

        // Sort the entries by counts in descending order
        List<Map.Entry<Block, Integer>> sortedEntries = new ArrayList<>(blockCountMap.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Create a LinkedHashMap to maintain the sorted order
        Map<Block, Integer> sortedBlockCounts = new LinkedHashMap<>();
        for (Map.Entry<Block, Integer> entry : sortedEntries) {
            sortedBlockCounts.put(entry.getKey(), entry.getValue());
        }

        // Update blockCounts with the sorted map
        this.blockCounts = sortedBlockCounts;
    }

    public boolean hasUpgradeCard(UpgradeCardType upgradeCardType) {
        for(int i = 1; i < 4; i++) {
            if(itemHandler.getStackInSlot(i).getItem().equals(upgradeCardType.getItem())) return true;
        }
        return false;
    }



                                  /**
     * @param state
     * @param pos
     * @return TRUE if able to destroy
     */
    private boolean canDestroy(BlockState state, BlockPos pos) {
        if (state.isAir()) return false;
        if (level.getBlockEntity(pos) != null) return false;
        if (state.getDestroySpeed(level, pos) <= 0) return false;
        if (state.liquid()) return false;
        if (state.is(Blocks.BEDROCK)) return false;
        if (state.is(ModBlocks.AUTO_MINER)) return false;
        return true;
    }

    private boolean destroyBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if(hasUpgradeCard(UpgradeCardType.REVEALING))
            sendPacket(pos);

        advancePosition(new ChunkPos(worldPosition), level.getMinBuildHeight());
        return level.destroyBlock(pos, false);
    }


    private void sendPacket(BlockPos brokenBlock) {
        if (level.isClientSide) return;
        PacketDistributor.sendToAllPlayers(new BlockBreakPayload(brokenBlock, worldPosition));
    }


    @Override
    public Component getDisplayName() {
        return Component.literal("tes2");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AutoMinerMenu(containerId, inventory, this, this.data);
    }

    public Map<Block, Integer> getBlockCounts() {
        return blockCounts;
    }

    public void updateBlockInfos() {
        blockCounts = getBlockCounts();
    }
}
