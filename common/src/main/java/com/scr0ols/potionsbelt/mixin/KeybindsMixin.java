package com.scr0ols.potionsbelt.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.scr0ols.potionsbelt.BeltKeybinds;
import com.scr0ols.potionsbelt.ClientBeltState;
import com.scr0ols.potionsbelt.OpenBeltMenuPayload;
import com.scr0ols.potionsbelt.PotionsBeltItem;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Vanilla-internals glue that has to run inside Minecraft#handleKeybinds
 * itself, ahead of vanilla's own key handling later in that same method --
 * a plain tick event fires too late to stop vanilla from having already
 * acted on a click. Confirmed on NeoForge 26.2 that handleKeybinds() is
 * still on Minecraft itself (not moved into the new Gui/Hud split -- Gui
 * gained its own handleKeybinds(), called partway through this one, but it
 * only handles toggle-GUI/advancements/social/chat/command keys, none of
 * which we touch), and that the hotbar-slot-switch loop and the inventory-
 * key loop we drain are still directly in Minecraft#handleKeybinds, in the
 * same order relative to HEAD. This class only ever holds private injector
 * methods (a @Mixin class can't expose normal callable methods -- Mixin
 * dissolves it into its target at load time; shared logic lives in
 * ClientBeltState instead).
 *
 * Two things happen here:
 * - While BeltKeybinds.SELECT_MODIFIER is held and the belt is held, the
 *   vanilla hotbar keys 1-9 are drained via consumeClick() (so the hotbar
 *   slot does NOT switch) and reinterpreted as column picks. This has to be
 *   gated behind a modifier: binding column-select directly to the bare 1-9
 *   keys collided with vanilla's own hotbar-slot keybindings sharing the
 *   same physical keys -- draining here, before vanilla's own hotbar-switch
 *   loop runs later in handleKeybinds, is what avoids that collision.
 * - The vanilla "Open/Close Inventory" key, when the belt is the selected
 *   (main hand) item, opens the belt menu instead of the player inventory.
 *
 * Both funnel through ClientBeltState#onColumnPicked / OpenBeltMenuPayload,
 * the same entry points BeltKeybinds' own dedicated "Open Belt Menu" key and
 * the mouse-scroll column cycling use, so no input method can drift from
 * another in behavior.
 *
 * If a column pick (via any input method) turns out empty while the player
 * is already mid-drink, the server cancels that drink (see
 * PotionsBeltItem#onColumnSelected). Right click is typically still held at
 * that point, which would immediately re-trigger a new drink of the first
 * available potion. To match "an empty pick ends this belt use" we set
 * ClientBeltState's suppression flag and block startUseItem() for the belt
 * until the use key is released, forcing a fresh press before another drink
 * can start.
 *
 * This mixin only applies on the client (see potionsbelt.mixins.json's
 * "client" array) -- Minecraft.class doesn't exist on a dedicated server's
 * classpath, so it must never be in the common "mixins" list.
 */
@Mixin(Minecraft.class)
public class KeybindsMixin {

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void potionsbelt$clearSuppression(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        LocalPlayer player = client.player;

        // Clear the suppression as soon as the use key is released (or the belt
        // is no longer held), so the next press starts a fresh belt use.
        if (ClientBeltState.isDrinkSuppressed()
                && (player == null || !client.options.keyUse.isDown() || !PotionsBeltItem.isHeldBy(player))) {
            ClientBeltState.clearDrinkSuppression();
        }
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void potionsbelt$interceptColumnSelection(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        LocalPlayer player = client.player;
        if (player == null || !BeltKeybinds.SELECT_MODIFIER.isDown() || !PotionsBeltItem.isHeldBy(player)) {
            return;
        }
        for (int i = 0; i < client.options.keyHotbarSlots.length; i++) {
            if (client.options.keyHotbarSlots[i].consumeClick()) {
                ClientBeltState.onColumnPicked(player, i + 1);
            }
        }
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void potionsbelt$interceptInventoryKey(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        LocalPlayer player = client.player;
        if (player == null || !(player.getMainHandItem().getItem() instanceof PotionsBeltItem)) {
            return;
        }
        if (client.options.keyInventory.consumeClick()) {
            ClientPacketDistributor.sendToServer(new OpenBeltMenuPayload());
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void potionsbelt$blockDrinkUntilRelease(CallbackInfo ci) {
        if (!ClientBeltState.isDrinkSuppressed()) {
            return;
        }
        LocalPlayer player = ((Minecraft) (Object) this).player;
        if (player != null && PotionsBeltItem.isHeldBy(player)) {
            ci.cancel();
        }
    }
}
