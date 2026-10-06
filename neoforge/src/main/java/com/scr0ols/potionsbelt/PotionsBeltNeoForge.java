package com.scr0ols.potionsbelt;

import com.scr0ols.potionsbelt.platform.NeoForgeRegistryHelper;
import com.scr0ols.potionsbelt.platform.Services;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(Constants.MOD_ID)
public class PotionsBeltNeoForge {

    public PotionsBeltNeoForge(IEventBus modEventBus) {
        CommonClass.init();
        ((NeoForgeRegistryHelper) Services.REGISTRY).registerAll(modEventBus);

        modEventBus.addListener(PotionsBeltNeoForge::addToCreativeTab);
        modEventBus.addListener(PotionsBeltNeoForge::registerPayloads);
        NeoForge.EVENT_BUS.addListener(PotionsBeltNeoForge::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> DelayedBottleClose.tick());

        Constants.LOG.info("Potion's Belt initialized on NeoForge");
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.POTIONS_BELT.get());
        }
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
