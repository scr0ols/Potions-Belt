package com.scr0ols.potionsbelt;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * While BeltKeybinds.SELECT_MODIFIER is held and the belt is held (either
 * hand), mouse-wheel scroll cycles its default column instead of switching
 * the hotbar slot -- the scroll-based alternative to the modifier+hotbar-key
 * column picks in KeybindsMixin. Gated behind the same modifier for the same
 * reason those are: without it, holding the belt would permanently steal
 * scroll-to-switch-hotbar-slot, which is a much bigger behavior change than a
 * held modifier key deserves.
 *
 * Each loader hooks vanilla's world-scroll (no screen open) and calls this;
 * when it returns true the loader cancels the vanilla scroll.
 */
public final class BeltScroll {

    private BeltScroll() {
    }

    public static boolean tryCycleColumn(double yOffset) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (yOffset == 0 || player == null
                || !BeltKeybinds.SELECT_MODIFIER.isDown() || !PotionsBeltItem.isHeldBy(player)) {
            return false;
        }

        // Scroll down (negative yOffset) advances the column forward, matching
        // João's testing feedback (2026-07-14) that the initial mapping felt
        // backwards.
        int direction = yOffset > 0 ? -1 : 1;
        int column = ((ClientBeltState.getDefaultColumn() - 1 + direction + BeltInventory.COLUMNS) % BeltInventory.COLUMNS) + 1;
        ClientBeltState.onColumnPicked(player, column);
        return true;
    }
}
