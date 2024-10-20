package com.tompkins_development.autominers.datagen;

import com.tompkins_development.autominers.block.ModBlocks;
import com.tompkins_development.autominers.block.entity.ModBlockEntities;
import com.tompkins_development.autominers.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        add(ModBlocks.FERRULITE_ORE.get(),
                block -> createOreDrop(ModBlocks.FERRULITE_ORE.get(), ModItems.RAW_FERRULITE.get()));

        dropSelf(ModBlocks.AUTO_MINER.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
       return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
