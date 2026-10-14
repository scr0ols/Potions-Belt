package com.scr0ols.potionsbelt.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface IClientNetworkHelper {

    void sendToServer(CustomPacketPayload payload);
}
