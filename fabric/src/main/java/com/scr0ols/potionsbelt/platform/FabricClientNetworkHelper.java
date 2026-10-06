package com.scr0ols.potionsbelt.platform;

import com.scr0ols.potionsbelt.platform.services.IClientNetworkHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class FabricClientNetworkHelper implements IClientNetworkHelper {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
