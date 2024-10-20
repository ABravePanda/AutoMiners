package com.tompkins_development.autominers.networking.payloads;

import com.tompkins_development.autominers.AutoMiners;
import com.tompkins_development.autominers.utils.BlockInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BlockBreakPayload(BlockPos brokenBlockPos, BlockPos blockEntityPos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlockBreakPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AutoMiners.MOD_ID, "block_break_notifier"));

    public static final StreamCodec<ByteBuf, BlockBreakPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            BlockBreakPayload::brokenBlockPos,
            BlockPos.STREAM_CODEC,
            BlockBreakPayload::blockEntityPos,
            BlockBreakPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
