package scr0ols.potionsbelt.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scr0ols.potionsbelt.BeltInventory;
import scr0ols.potionsbelt.PotionsBeltItem;
import scr0ols.potionsbelt.SelectColumnPayload;

/**
 * Client-side handling for the belt's hotbar-key column selection.
 *
 * While the player is drinking from the belt, hotbar keys 1-9 pick a column
 * instead of switching the held slot (intercepted at the head of
 * handleKeybinds() so the click is drained via consumeClick() before
 * vanilla's own hotbar-switch loop later in the same method gets to it).
 *
 * If the picked column is empty the server cancels the drink, but right click
 * is typically still held, which would immediately re-trigger a new drink of
 * the first available potion. To match "an empty selection ends this belt use"
 * we set {@link #suppressBeltDrinkUntilRelease} on an empty pick and block
 * startUseItem() for the belt until the use key is released, forcing a fresh
 * press before another drink can start. Picking a column that has a potion is
 * unaffected, and plain right click (no column) still drinks first-available.
 */
@Mixin(Minecraft.class)
public class KeybindsMixin {

    private static boolean suppressBeltDrinkUntilRelease = false;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void potionsbelt$interceptColumnSelection(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        LocalPlayer player = client.player;

        // Clear the suppression as soon as the use key is released (or the belt
        // is no longer held), so the next press starts a fresh belt use.
        if (suppressBeltDrinkUntilRelease
                && (player == null || !client.options.keyUse.isDown() || !holdingBelt(player))) {
            suppressBeltDrinkUntilRelease = false;
        }

        if (player == null || !player.isUsingItem()
                || !(player.getUseItem().getItem() instanceof PotionsBeltItem)) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (client.options.keyHotbarSlots[i].consumeClick()) {
                int column = i + 1;
                // Empty column: the server will cancel the drink; also require a
                // release + re-press before another belt drink can start, instead
                // of the still-held click re-triggering one immediately.
                if (BeltInventory.firstPotionSlotInColumn(player.getUseItem(), column) < 0) {
                    suppressBeltDrinkUntilRelease = true;
                }
                ClientPlayNetworking.send(new SelectColumnPayload(column));
            }
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void potionsbelt$blockDrinkUntilRelease(CallbackInfo ci) {
        if (!suppressBeltDrinkUntilRelease) {
            return;
        }
        LocalPlayer player = ((Minecraft) (Object) this).player;
        if (player != null && holdingBelt(player)) {
            ci.cancel();
        }
    }

    private static boolean holdingBelt(LocalPlayer player) {
        return player.getMainHandItem().getItem() instanceof PotionsBeltItem
                || player.getOffhandItem().getItem() instanceof PotionsBeltItem;
    }
}
