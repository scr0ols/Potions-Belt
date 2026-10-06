package com.scr0ols.potionsbelt;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class PotionsBeltNeoForge {

    public PotionsBeltNeoForge(IEventBus eventBus) {
        Constants.LOG.info("Potion's Belt skeleton loaded on NeoForge");
    }
}
