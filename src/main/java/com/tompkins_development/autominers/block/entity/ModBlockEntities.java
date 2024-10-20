package com.tompkins_development.autominers.block.entity;

import com.tompkins_development.autominers.AutoMiners;
import com.tompkins_development.autominers.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AutoMiners.MOD_ID);

    public static final Supplier<BlockEntityType<?>> AUTO_MINER = BLOCK_ENTITY_TYPES.register("autominer",
            () -> BlockEntityType.Builder.of(
                    AutoMinerBlockEntity::new,
                    ModBlocks.AUTO_MINER.get()
            ).build(null)
    );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
