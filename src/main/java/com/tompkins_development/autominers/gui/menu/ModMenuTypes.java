package com.tompkins_development.autominers.gui.menu;

import com.tompkins_development.autominers.AutoMiners;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, AutoMiners.MOD_ID);

    public static final Supplier<MenuType<AutoMinerMenu>> AUTO_MINER = MENU_TYPES.register("autominer",
            () -> IMenuTypeExtension.create(AutoMinerMenu::new)
    );

    public static void register(IEventBus bus) {
        MENU_TYPES.register(bus);
    }

}
