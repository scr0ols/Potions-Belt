package scr0ols.potionsbelt;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PotionsBelt implements ModInitializer {
	public static final String MOD_ID = "potions-belt";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.initialize();

		LOGGER.info("Potion's Belt initialized");
	}
}
