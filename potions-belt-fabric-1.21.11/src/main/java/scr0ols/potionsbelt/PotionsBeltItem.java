package scr0ols.potionsbelt;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;

public class PotionsBeltItem extends Item {

    public PotionsBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack belt = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, p) ->
                                new PotionsBeltMenu(containerId, playerInventory,
                                        BeltInventory.load(belt), belt),
                        belt.getHoverName()));
            }
            return InteractionResult.SUCCESS;
        }
        // Plain right click: drink the first available potion. The belt's
        // CONSUMABLE component (DEFAULT_DRINK) provides the vanilla 1.6 s drink
        // animation and sounds; the potion is only consumed at finishUsingItem.
        if (BeltInventory.hasPotion(belt)) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        if (player.getCooldowns().isOnCooldown(belt)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide()) {
            player.displayClientMessage(
                    Component.translatable("potions-belt.belt.no_potions"), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BUNDLE_INSERT_FAIL, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
        // Prevents the fail sound/message from spamming every ~4 ticks while
        // right click is held down (same pattern as vanilla ender pearl).
        player.getCooldowns().addCooldown(belt, 20);
        return InteractionResult.FAIL;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack belt, Level level, LivingEntity entity) {
        // Never call super: the default would consume the belt itself.
        if (!level.isClientSide()) {
            Player player = entity instanceof Player p ? p : null;
            int pendingColumn = player != null ? BeltSelections.get(player) : -1;
            int slot = pendingColumn > 0
                    ? BeltInventory.firstPotionSlotInColumn(belt, pendingColumn)
                    : BeltInventory.firstPotionSlot(belt);

            if (slot >= 0) {
                boolean keepPotion = player != null && player.hasInfiniteMaterials();
                ItemStack potion = BeltInventory.takePotionAt(belt, slot, keepPotion);
                Consumable consumable = potion.get(DataComponents.CONSUMABLE);
                if (consumable != null) {
                    // Applies the potion exactly like drinking it normally:
                    // effects, stats, advancement trigger, finish sounds.
                    consumable.onConsume(level, entity, potion);
                }
            } else if (pendingColumn > 0 && player != null) {
                // Column selected but empty (or bottles only) in all 3 rows:
                // abort with the same feedback as an empty belt, same as
                // if the column key had never been pressed.
                player.displayClientMessage(
                        Component.translatable("potions-belt.belt.no_potions"), true);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUNDLE_INSERT_FAIL, SoundSource.PLAYERS, 0.5F, 1.0F);
            }

            if (player != null) {
                BeltSelections.clear(player);
            }
        }
        return belt;
    }

    @Override
    public boolean releaseUsing(ItemStack belt, Level level, LivingEntity entity, int timeCharged) {
        // Drink released early or interrupted (hotbar switch, drop, etc.):
        // clear the pending column so a later drink doesn't reuse it.
        if (!level.isClientSide() && entity instanceof Player player) {
            BeltSelections.clear(player);
        }
        return false;
    }
}
