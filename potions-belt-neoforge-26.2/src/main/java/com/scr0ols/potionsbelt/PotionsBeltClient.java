package com.scr0ols.potionsbelt;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = PotionsBelt.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = PotionsBelt.MOD_ID, value = Dist.CLIENT)
public class PotionsBeltClient {

    @SubscribeEvent
    static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PotionsBelt.POTIONS_BELT_MENU.get(), PotionsBeltScreen::new);
    }
}
