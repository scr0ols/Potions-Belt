package com.scr0ols.potionsbelt;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** C2S: player pressed number key `column` (1-9) while drinking from the belt. */
public record SelectColumnPayload(int column) implements CustomPacketPayload {

    public static final Type<SelectColumnPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, "select_column"));

    public static final StreamCodec<ByteBuf, SelectColumnPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, SelectColumnPayload::column, SelectColumnPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
