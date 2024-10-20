package com.tompkins_development.autominers.datagen;

import com.tompkins_development.autominers.AutoMiners;
import com.tompkins_development.autominers.item.ModItems;
import com.tompkins_development.autominers.utils.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, AutoMiners.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.DRILLS)
                .add(ModItems.STONE_DRILL.get());

        tag(ModTags.Items.UPGRADE_CARDS)
                .add(ModItems.BLANK_UPGRADE_CARD.get())
                .add(ModItems.FORTUNE_UPGRADE_CARD.get())
                .add(ModItems.REVEALING_UPGRADE_CARD.get())
                .add(ModItems.SPEED_UPGRADE_CARD.get());


    }
}
