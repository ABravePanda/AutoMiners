package com.tompkins_development.autominers.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LevelUtils {

    public static List<ItemStack> getDrops(ServerLevel level, BlockPos blockPos, BlockState blockState) {
        LootParams.Builder lootBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE))
                .withParameter(LootContextParams.BLOCK_STATE, blockState);

        // Get the drops using the updated loot system
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(blockState.getBlock().getLootTable());
        return lootTable.getRandomItems(lootBuilder.create(LootContextParamSets.BLOCK));
    }
}
