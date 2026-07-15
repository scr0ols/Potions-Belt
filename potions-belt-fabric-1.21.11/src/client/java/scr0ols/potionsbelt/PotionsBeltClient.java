package scr0ols.potionsbelt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.Identifier;

public class PotionsBeltClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(PotionsBelt.POTIONS_BELT_MENU, PotionsBeltScreen::new);

		HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR,
				Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, "belt_preview"), BeltHud::render);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientBeltState.reset());

		BeltKeybinds.initialize();
	}
}
