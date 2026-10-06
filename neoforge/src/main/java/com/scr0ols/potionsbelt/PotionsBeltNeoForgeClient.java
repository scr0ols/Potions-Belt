package com.scr0ols.potionsbelt;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.minecraft.resources.Identifier;

@Mod(value = PotionsBelt.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = PotionsBelt.MOD_ID, value = Dist.CLIENT)
public class PotionsBeltClient {

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PotionsBelt.POTIONS_BELT_MENU.get(), PotionsBeltScreen::new);
    }

    @SubscribeEvent
    static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, "belt_preview"),
                BeltHud::render);
    }

    // Not IModBusEvent - fired on NeoForge.EVENT_BUS, still auto-routed there by @EventBusSubscriber.
    @SubscribeEvent
    static void onLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientBeltState.reset();
    }
}
