package com.scr0ols.potionsbelt;

import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Client-side mirror of the player's sticky belt default column (1-9), plus
 * the "suppress a belt drink from restarting itself" flag used when an empty
 * column pick cuts a drink short. Kept in sync locally the instant a column
 * is picked (no need to wait for a server round trip), and used by BeltHud
 * to preview the potion that would actually be drunk. Reset on disconnect,
 * matching the server clearing BeltSelections.
 *
 * onColumnPicked is the single entry point every column-selection input
 * (BeltKeybinds' dedicated keys, MouseScrollMixin's scroll cycling) funnels
 * through, so they can't drift in behavior. It's plain code, not mixin code
 * -- KeybindsMixin only holds its two private injectors, since a @Mixin
 * class can't expose normal callable methods (Mixin dissolves it into its
 * target at load time; anything else in the class must be a recognized
 * injector or it fails to apply).
 */
public final class ClientBeltState {

    private static int defaultColumn = 1;
    private static boolean suppressDrinkUntilRelease = false;

    private ClientBeltState() {
    }

    public static int getDefaultColumn() {
        return defaultColumn;
    }

    public static void reset() {
        defaultColumn = 1;
        suppressDrinkUntilRelease = false;
    }

    public static boolean isDrinkSuppressed() {
        return suppressDrinkUntilRelease;
    }

    public static void clearDrinkSuppression() {
        suppressDrinkUntilRelease = false;
    }

    /** Sets the player's default column and notifies the server, from any input method. */
    public static void onColumnPicked(LocalPlayer player, int column) {
        // Sticky: this becomes the player's default column for future drinks
        // too, not just the one in progress.
        defaultColumn = column;
        // Empty column while mid-drink: the server will cancel that drink;
        // also require a release + re-press before another belt drink can
        // start, instead of the still-held click re-triggering one immediately.
        if (player.isUsingItem() && player.getUseItem().getItem() instanceof PotionsBeltItem
                && BeltInventory.firstPotionSlotInColumn(player.getUseItem(), column) < 0) {
            suppressDrinkUntilRelease = true;
        }
        ClientPacketDistributor.sendToServer(new SelectColumnPayload(column));
    }
}
