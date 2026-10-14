package com.scr0ols.potionsbelt;

import com.scr0ols.potionsbelt.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ModMenus {

    private static final Supplier<MenuType<?>> BELT = Services.REGISTRY.register(
            BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(Constants.MOD_ID, "potions_belt"),
            () -> Services.REGISTRY.createMenuType(PotionsBeltMenu::new));

    @SuppressWarnings("unchecked")
    public static final Supplier<MenuType<PotionsBeltMenu>> POTIONS_BELT =
            () -> (MenuType<PotionsBeltMenu>) BELT.get();

    private ModMenus() {
    }

    /** Forces the class to load, which queues the registration above. */
    public static void init() {
    }
}
