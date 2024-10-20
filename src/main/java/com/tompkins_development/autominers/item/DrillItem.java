package com.tompkins_development.autominers.item;

import net.minecraft.world.item.Item;

public class DrillItem extends Item {

    public DrillItem(int durability) {
        super(new Item.Properties().durability(durability));
    }
}
