package com.tompkins_development.autominers.item;

import com.tompkins_development.autominers.AutoMiners;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AutoMiners.MOD_ID);

    public static final DeferredItem<Item> RAW_FERRULITE = ITEMS.register("raw_ferrulite",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FERRULITE_INGOT = ITEMS.register("ferrulite_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FERRULITE_NUGGET = ITEMS.register("ferrulite_nugget",
            () -> new Item(new Item.Properties()));


    public static final DeferredItem<Item> STONE_DRILL = ITEMS.register("stone_drill",
            () -> new DrillItem(500));


    public static final DeferredItem<Item> BLANK_UPGRADE_CARD = ITEMS.register("blank_upgrade_card",
            () -> new UpgradeCardItem(16));

    public static final DeferredItem<Item> FORTUNE_UPGRADE_CARD = ITEMS.register("fortune_upgrade_card",
            () -> new UpgradeCardItem(4));

    public static final DeferredItem<Item> SPEED_UPGRADE_CARD = ITEMS.register("speed_upgrade_card",
            () -> new UpgradeCardItem(4));

    public static final DeferredItem<Item> REVEALING_UPGRADE_CARD = ITEMS.register("revealing_upgrade_card",
            () -> new UpgradeCardItem(1));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
