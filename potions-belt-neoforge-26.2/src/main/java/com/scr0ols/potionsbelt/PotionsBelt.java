package com.scr0ols.potionsbelt;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
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
        ModItems.initialize(modEventBus);
        ModSounds.initialize(modEventBus);

        modEventBus.addListener(PotionsBelt::registerPayloads);
        NeoForge.EVENT_BUS.addListener(PotionsBelt::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(DelayedBottleClose::tick);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(SelectColumnPayload.TYPE, SelectColumnPayload.STREAM_CODEC,
                (payload, context) -> PotionsBeltItem.onColumnSelected(context.player(), payload.column()));
        registrar.playToServer(OpenBeltMenuPayload.TYPE, OpenBeltMenuPayload.STREAM_CODEC,
                (payload, context) -> PotionsBeltItem.openMenu(context.player()));
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        BeltSelections.clear(event.getEntity());
    }
}
