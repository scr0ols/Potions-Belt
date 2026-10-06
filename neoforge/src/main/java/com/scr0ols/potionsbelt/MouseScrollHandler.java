package com.scr0ols.potionsbelt;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

/**
 * NeoForge 26.2 has a native, cancellable InputEvent.MouseScrollingEvent,
 * fired from the no-screen-open, world-scroll branch of MouseHandler#onScroll
 * (via ClientHooks.onMouseScroll) before the vanilla hotbar-slot switch runs,
 * so unlike the Fabric mixin there is no need to check for an open screen.
 * The column-cycling itself is shared, see BeltScroll.
 */
@EventBusSubscriber(modid = Constants.MOD_ID, value = Dist.CLIENT)
public final class MouseScrollHandler {

    private MouseScrollHandler() {
    }

    @SubscribeEvent
    static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (BeltScroll.tryCycleColumn(event.getScrollDeltaY())) {
            event.setCanceled(true);
        }
    }
}
