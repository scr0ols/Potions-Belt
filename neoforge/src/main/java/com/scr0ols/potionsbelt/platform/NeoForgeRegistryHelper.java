package com.scr0ols.potionsbelt.platform;

import com.scr0ols.potionsbelt.Constants;
import com.scr0ols.potionsbelt.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {

    private final Map<ResourceKey<?>, DeferredRegister<?>> registers = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(Registry<T> registry, Identifier id, Supplier<? extends T> factory) {
        DeferredRegister<T> register = (DeferredRegister<T>) registers.computeIfAbsent(
                registry.key(), key -> DeferredRegister.create(registry, Constants.MOD_ID));
        return register.register(id.getPath(), factory);
    }

    /** Hooks every register created so far to the mod event bus; call after the common init. */
    public void registerAll(IEventBus modEventBus) {
        registers.values().forEach(register -> register.register(modEventBus));
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(MenuFactory<T> factory) {
        return new MenuType<>(factory::create, FeatureFlags.VANILLA_SET);
    }
}
