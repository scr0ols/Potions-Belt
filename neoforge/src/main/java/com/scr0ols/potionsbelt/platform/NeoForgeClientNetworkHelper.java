package com.scr0ols.potionsbelt.platform;

import com.scr0ols.potionsbelt.platform.services.IClientNetworkHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class NeoForgeClientNetworkHelper implements IClientNetworkHelper {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPacketDistributor.sendToServer(payload);
    }
}
