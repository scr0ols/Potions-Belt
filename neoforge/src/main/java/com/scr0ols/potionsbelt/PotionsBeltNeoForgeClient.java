package com.scr0ols.potionsbelt;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public class PotionsBeltNeoForgeClient {

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.POTIONS_BELT.get(), PotionsBeltScreen::new);
    }

    @SubscribeEvent
    static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "belt_preview"),
                BeltHud::render);
    }

    @SubscribeEvent
    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(BeltKeybinds.CATEGORY);
        event.register(BeltKeybinds.OPEN_MENU);
        event.register(BeltKeybinds.SELECT_MODIFIER);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        BeltKeybinds.tick(Minecraft.getInstance());
    }

    // Not IModBusEvent - fired on NeoForge.EVENT_BUS, still auto-routed there by @EventBusSubscriber.
    @SubscribeEvent
    static void onLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientBeltState.reset();
    }
}
