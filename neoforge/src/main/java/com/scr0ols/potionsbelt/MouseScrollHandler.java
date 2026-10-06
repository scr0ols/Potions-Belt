package com.scr0ols.potionsbelt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

/**
 * While BeltKeybinds.SELECT_MODIFIER is held, the belt is held (either
 * hand), and no screen is open, mouse-wheel scroll cycles its default column
 * instead of switching the hotbar slot -- the scroll-based alternative to
 * the modifier+hotbar-key column picks in KeybindsMixin. Gated behind the
 * same modifier for the same reason those are: without it, holding the belt
 * would permanently steal scroll-to-switch-hotbar-slot, which is a much
 * bigger behavior change than a held modifier key deserves.
 *
 * On Fabric this needed a mixin into MouseHandler#onScroll directly, since
 * no Fabric API event covered world mouse scroll. NeoForge 26.2 has a
 * native, cancellable InputEvent.MouseScrollingEvent instead -- confirmed
 * via the decompiled MouseHandler#onScroll source that it's fired from
 * exactly the right spot: the no-screen-open, world-scroll branch, via
 * ClientHooks.onMouseScroll, *before* the vanilla hotbar-slot switch
 * (Inventory#setSelectedSlot) runs, and its own screen-open case goes
 * through an entirely separate onScreenMouseScrollPre/Post path, so this
 * event genuinely never fires over an open screen -- no need to re-check
 * that ourselves the way the Fabric mixin had to. This is the "likely a net
 * win" case from the original port baseline, now confirmed rather than
 * assumed.
 */
@EventBusSubscriber(modid = PotionsBelt.MOD_ID, value = Dist.CLIENT)
public final class MouseScrollHandler {

    private MouseScrollHandler() {
    }

    @SubscribeEvent
    static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        double yOffset = event.getScrollDeltaY();
        if (yOffset == 0 || player == null
                || !BeltKeybinds.SELECT_MODIFIER.isDown() || !PotionsBeltItem.isHeldBy(player)) {
            return;
        }

        // Scroll down (negative yOffset) advances the column forward, matching
        // João's testing feedback (2026-07-14, Fabric side) that the initial
        // mapping felt backwards.
        int direction = yOffset > 0 ? -1 : 1;
        int column = ((ClientBeltState.getDefaultColumn() - 1 + direction + BeltInventory.COLUMNS) % BeltInventory.COLUMNS) + 1;
        ClientBeltState.onColumnPicked(player, column);
        event.setCanceled(true);
    }
}
