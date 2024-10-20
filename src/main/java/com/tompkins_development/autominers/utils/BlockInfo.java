package com.tompkins_development.autominers.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class BlockInfo {

    private int id;
    private int count;

    public BlockInfo(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public BlockInfo(ByteBuf buf) {
        id = buf.readInt();
        count = buf.readInt();
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int newCount) {
        this.count = newCount;
    }

    public void encode(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(count);
    }

    public static final StreamCodec<ByteBuf,BlockInfo> STREAM_CODEC = StreamCodec.ofMember(BlockInfo::encode, BlockInfo::new);


}
