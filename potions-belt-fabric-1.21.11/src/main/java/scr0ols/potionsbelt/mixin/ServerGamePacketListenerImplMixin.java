package scr0ols.potionsbelt.mixin;

import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scr0ols.potionsbelt.DelayedBottleClose;
import scr0ols.potionsbelt.PotionsBeltItem;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    /**
     * handleSetCarriedItem() -- the server's handler for the hotbar-slot-
     * switch packet -- calls player.stopUsingItem() directly the moment the
     * slot actually changes while the main hand is the one being used, well
     * before the selected slot itself is updated. This is a separate bypass
     * from LivingEntityMixin's: verified via javap disassembly that this
     * call happens ahead of (and instead of) updatingUsingItem() ever
     * detecting a mismatch on its own -- by the time the next tick's
     * updatingUsingItem() would run, isUsingItem() is already false, so its
     * mismatch branch (and LivingEntityMixin's injection into it) never
     * fires for this specific interaction. This is the actual mechanism
     * behind switching hotbar slot mid-drink, which LivingEntityMixin alone
     * doesn't cover.
     */
    @Inject(method = "handleSetCarriedItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;stopUsingItem()V"))
    private void potionsBelt$onHotbarSlotSwitchedMidUse(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
        ServerGamePacketListenerImpl self = (ServerGamePacketListenerImpl) (Object) this;
        if (self.player.getUseItem().getItem() instanceof PotionsBeltItem) {
            DelayedBottleClose.playImmediately(self.player);
        }
    }
}
