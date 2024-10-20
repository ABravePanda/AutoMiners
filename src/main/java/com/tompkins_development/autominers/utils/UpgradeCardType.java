package com.tompkins_development.autominers.utils;

import com.tompkins_development.autominers.item.ModItems;
import net.minecraft.world.item.Item;

public enum UpgradeCardType {
    REVEALING(ModItems.REVEALING_UPGRADE_CARD.get()),
    SPEED(ModItems.SPEED_UPGRADE_CARD.get()),
    FORTUNE(ModItems.FORTUNE_UPGRADE_CARD.get());

    private Item item;

    UpgradeCardType(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
