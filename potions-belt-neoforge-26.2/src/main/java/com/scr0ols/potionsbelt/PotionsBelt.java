package com.scr0ols.potionsbelt;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(PotionsBelt.MOD_ID)
public class PotionsBelt {
    public static final String MOD_ID = "potionsbelt";

    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<PotionsBeltMenu>> POTIONS_BELT_MENU =
            MENUS.register("potions_belt", () -> new MenuType<>(PotionsBeltMenu::new, FeatureFlags.VANILLA_SET));

    public PotionsBelt(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
