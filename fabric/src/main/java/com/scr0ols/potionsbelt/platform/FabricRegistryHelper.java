package com.scr0ols.potionsbelt.platform;

import com.scr0ols.potionsbelt.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public <T> Supplier<T> register(Registry<T> registry, Identifier id, Supplier<? extends T> factory) {
        T value = Registry.register(registry, id, factory.get());
        return () -> value;
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        return new MenuType<>(factory::create, FeatureFlags.VANILLA_SET);
    }
}
