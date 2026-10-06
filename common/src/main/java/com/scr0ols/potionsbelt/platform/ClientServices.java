package com.scr0ols.potionsbelt.platform;

import com.scr0ols.potionsbelt.platform.services.IClientNetworkHelper;

/**
 * Client-only platform services, kept apart from {@link Services} so a
 * dedicated server never loads the client implementations.
 */
public class ClientServices {

    public static final IClientNetworkHelper NETWORK = Services.load(IClientNetworkHelper.class);

    private ClientServices() {
    }
}
