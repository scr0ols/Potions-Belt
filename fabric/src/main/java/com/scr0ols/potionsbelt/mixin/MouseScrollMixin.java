package com.scr0ols.potionsbelt.mixin;

import com.scr0ols.potionsbelt.BeltScroll;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * No Fabric API event covers world mouse scroll, so this intercepts vanilla's
 * own MouseHandler#onScroll directly and cancels it only when BeltScroll
 * consumed the scroll; every other scroll (in a screen, modifier not held)
 * is left untouched. NeoForge has a native event for this instead.
 */
@Mixin(MouseHandler.class)
public class MouseScrollMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void potionsbelt$cycleColumnOnScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        if (Minecraft.getInstance().gui.screen() == null && BeltScroll.tryCycleColumn(yOffset)) {
            ci.cancel();
        }
    }
}
