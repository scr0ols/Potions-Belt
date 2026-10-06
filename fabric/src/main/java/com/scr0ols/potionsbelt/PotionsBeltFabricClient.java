package com.scr0ols.potionsbelt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.Identifier;

public class PotionsBeltFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenus.POTIONS_BELT.get(), PotionsBeltScreen::new);

        HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(Constants.MOD_ID, "belt_preview"), BeltHud::render);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientBeltState.reset());

        // Registers the category into vanilla's sort order; BeltKeybinds.CATEGORY
        // is the same record value, so the mappings below resolve to it.
        KeyMapping.Category.register(BeltKeybinds.CATEGORY.id());
        KeyMappingHelper.registerKeyMapping(BeltKeybinds.OPEN_MENU);
        KeyMappingHelper.registerKeyMapping(BeltKeybinds.SELECT_MODIFIER);
        ClientTickEvents.END_CLIENT_TICK.register(BeltKeybinds::tick);
    }
}
