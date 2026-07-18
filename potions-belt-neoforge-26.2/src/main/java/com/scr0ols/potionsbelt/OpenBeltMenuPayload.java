package com.scr0ols.potionsbelt;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** C2S: player pressed the dedicated "Open Belt Menu" keybind while holding the belt. */
public record OpenBeltMenuPayload() implements CustomPacketPayload {

    public static final Type<OpenBeltMenuPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, "open_belt_menu"));

    public static final StreamCodec<ByteBuf, OpenBeltMenuPayload> STREAM_CODEC =
            StreamCodec.unit(new OpenBeltMenuPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
