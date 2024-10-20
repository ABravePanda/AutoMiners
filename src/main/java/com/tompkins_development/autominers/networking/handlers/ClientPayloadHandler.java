package com.tompkins_development.autominers.networking.handlers;

import com.tompkins_development.autominers.block.entity.AutoMinerBlockEntity;
import com.tompkins_development.autominers.networking.payloads.BlockBreakPayload;
import com.tompkins_development.autominers.utils.BlockInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Iterator;
import java.util.List;

public class ClientPayloadHandler {
    public static void handleBlockBreak(BlockBreakPayload blockBreakPayload, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> {
                    BlockEntity entity = Minecraft.getInstance().level.getBlockEntity(blockBreakPayload.blockEntityPos());
                    if(entity instanceof AutoMinerBlockEntity autoMinerBlockEntity) {
                        autoMinerBlockEntity.updateBlockCounts();
                    }
                }
        );
    }
}

