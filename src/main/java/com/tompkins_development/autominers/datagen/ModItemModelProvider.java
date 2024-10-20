package com.tompkins_development.autominers.datagen;

import com.tompkins_development.autominers.AutoMiners;
import com.tompkins_development.autominers.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, AutoMiners.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.STONE_DRILL.get());

        basicItem(ModItems.RAW_FERRULITE.get());
        basicItem(ModItems.FERRULITE_INGOT.get());
        basicItem(ModItems.FERRULITE_NUGGET.get());


        basicItem(ModItems.BLANK_UPGRADE_CARD.get());
        basicItem(ModItems.FORTUNE_UPGRADE_CARD.get());
        basicItem(ModItems.SPEED_UPGRADE_CARD.get());
        basicItem(ModItems.REVEALING_UPGRADE_CARD.get());
    }
}
