package scr0ols.potionsbelt;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.gui.screens.MenuScreens;

public class PotionsBeltClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(PotionsBelt.POTIONS_BELT_MENU, PotionsBeltScreen::new);
	}
}
