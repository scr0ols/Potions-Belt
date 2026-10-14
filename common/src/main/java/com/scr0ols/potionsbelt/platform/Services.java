package com.scr0ols.potionsbelt.platform;

import com.scr0ols.potionsbelt.Constants;
import com.scr0ols.potionsbelt.platform.services.IRegistryHelper;

import java.util.ServiceLoader;

/** Locates the loader-specific implementations of the interfaces in {@code platform.services}. */
public class Services {

    public static final IRegistryHelper REGISTRY = load(IRegistryHelper.class);

    static <T> T load(Class<T> clazz) {
        T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
