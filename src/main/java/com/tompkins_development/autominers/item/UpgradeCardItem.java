package com.tompkins_development.autominers.item;

import net.minecraft.world.item.Item;

public class UpgradeCardItem extends Item {



    public UpgradeCardItem(int stacksTo) {
        super(new Item.Properties().stacksTo(stacksTo));
    }
}
