package com.scr0ols.potionsbelt.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

/**
 * Loader-specific registration. Fabric registers the entry right away, NeoForge
 * defers it to the registry event; either way the returned supplier is only
 * safe to call once registration has happened.
 */
public interface IRegistryHelper {

    <T> Supplier<T> register(Registry<T> registry, Identifier id, Supplier<? extends T> factory);

    /** MenuType's constructor is private in plain vanilla, both loaders widen it. */
    <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory);

    /** Stand-in for MenuType.MenuSupplier, which is private in plain vanilla. */
    @FunctionalInterface
    interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int containerId, Inventory inventory);
    }
}
