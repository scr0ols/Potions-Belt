package scr0ols.potionsbelt.mixin.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scr0ols.potionsbelt.PotionsBeltItem;
import scr0ols.potionsbelt.SelectColumnPayload;

/**
 * While the player is drinking from the belt, hotbar keys 1-9 pick a column
 * instead of switching the held slot. Injected at the head of
 * handleKeybinds() so the click is drained (via consumeClick()) before
 * vanilla's own hotbar-switch loop later in the same method gets to it.
 */
@Mixin(Minecraft.class)
public class KeybindsMixin {

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void potionsbelt$interceptColumnSelection(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        LocalPlayer player = client.player;
        if (player == null || !player.isUsingItem()
                || !(player.getUseItem().getItem() instanceof PotionsBeltItem)) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            if (client.options.keyHotbarSlots[i].consumeClick()) {
                ClientPlayNetworking.send(new SelectColumnPayload(i + 1));
            }
        }
    }
}
