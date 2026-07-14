package scr0ols.potionsbelt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PotionsBelt implements ModInitializer {
	public static final String MOD_ID = "potions-belt";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final MenuType<PotionsBeltMenu> POTIONS_BELT_MENU = Registry.register(
			BuiltInRegistries.MENU,
			Identifier.fromNamespaceAndPath(MOD_ID, "potions_belt"),
			new MenuType<>(PotionsBeltMenu::new, FeatureFlags.VANILLA_SET));

	@Override
	public void onInitialize() {
		ModItems.initialize();

		PayloadTypeRegistry.playC2S().register(SelectColumnPayload.TYPE, SelectColumnPayload.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(SelectColumnPayload.TYPE,
				(payload, context) -> PotionsBeltItem.onColumnSelected(context.player(), payload.column()));
		ServerPlayConnectionEvents.DISCONNECT.register(
				(handler, server) -> BeltSelections.clear(handler.getPlayer()));

		LOGGER.info("Potion's Belt initialized");
	}
}
