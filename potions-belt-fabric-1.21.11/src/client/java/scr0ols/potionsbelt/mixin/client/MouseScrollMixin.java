package scr0ols.potionsbelt.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scr0ols.potionsbelt.BeltInventory;
import scr0ols.potionsbelt.BeltKeybinds;
import scr0ols.potionsbelt.ClientBeltState;
import scr0ols.potionsbelt.PotionsBeltItem;

/**
 * While BeltKeybinds.SELECT_MODIFIER is held, the belt is held (either
 * hand), and no screen is open, mouse-wheel scroll cycles its default column
 * instead of switching the hotbar slot -- the scroll-based alternative to
 * the modifier+hotbar-key column picks in KeybindsMixin. Gated behind the
 * same modifier for the same reason those are: without it, holding the belt
 * would permanently steal scroll-to-switch-hotbar-slot, which is a much
 * bigger behavior change than a held modifier key deserves (see NOTES.md
 * 2026-07-14, "OK BUT" on the scroll-gating checklist item). No Fabric API
 * event covers world mouse scroll, so this intercepts vanilla's own
 * MouseHandler#onScroll directly and cancels it for this one case only;
 * every other scroll (in a screen, spectator, modifier not held) is left
 * untouched.
 */
@Mixin(MouseHandler.class)
public class MouseScrollMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void potionsbelt$cycleColumnOnScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (yOffset == 0 || client.screen != null || player == null
                || !BeltKeybinds.SELECT_MODIFIER.isDown() || !PotionsBeltItem.isHeldBy(player)) {
            return;
        }

        // Scroll down (negative yOffset) advances the column forward, matching
        // João's testing feedback (2026-07-14) that the initial mapping felt
        // backwards.
        int direction = yOffset > 0 ? -1 : 1;
        int column = ((ClientBeltState.getDefaultColumn() - 1 + direction + BeltInventory.COLUMNS) % BeltInventory.COLUMNS) + 1;
        ClientBeltState.onColumnPicked(player, column);
        ci.cancel();
    }
}
