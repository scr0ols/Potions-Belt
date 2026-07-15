package scr0ols.potionsbelt.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import scr0ols.potionsbelt.DelayedBottleClose;
import scr0ols.potionsbelt.PotionsBeltItem;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected ItemStack useItem;

    /**
     * updatingUsingItem() (called every tick from tick()) calls
     * stopUsingItem() directly, bypassing Item#releaseUsing entirely,
     * whenever the item in the used hand no longer matches what use started
     * with -- this is what actually happens on a hotbar-slot switch or the
     * item being removed from hand mid-use (verified via javap disassembly:
     * PotionsBeltItem's own releaseUsing() override, hooked to
     * ItemStack#releaseUsing, is never reached on this path). useItem here
     * still holds the belt stack, since it's only cleared inside
     * stopUsingItem() itself, which hasn't run yet at this injection point.
     */
    @Inject(method = "updatingUsingItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;stopUsingItem()V"))
    private void potionsBelt$onItemSwitchedMidUse(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide()
                || !(self instanceof Player player)
                || !(useItem.getItem() instanceof PotionsBeltItem)) {
            return;
        }
        DelayedBottleClose.playImmediately(self);
    }
}
