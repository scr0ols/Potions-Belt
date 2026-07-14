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
            announceNoPotions(player, level);
        }
        // Prevents the fail sound/message from spamming every ~4 ticks while
        // right click is held down (same pattern as vanilla ender pearl).
        player.getCooldowns().addCooldown(belt, 20);
        return InteractionResult.FAIL;
    }

    /**
     * Called server-side when a SelectColumnPayload arrives. Records the
     * pending column, then, if the player is currently drinking from the
     * belt and that column is already known to be empty, cuts the drink
     * animation short right away instead of making the player wait for the
     * full 1.6 s just to see the same "nothing to drink" feedback at the end.
     */
    public static void onColumnSelected(Player player, int column) {
        BeltSelections.set(player, column);
        if (!player.isUsingItem()) {
            return;
        }
        ItemStack belt = player.getUseItem();
        if (!(belt.getItem() instanceof PotionsBeltItem)
                || BeltInventory.firstPotionSlotInColumn(belt, column) >= 0) {
            return;
        }
        announceNoPotions(player, player.level());
        player.stopUsingItem();
    }

    private static void announceNoPotions(Player player, Level level) {
        player.displayClientMessage(
                Component.translatable("potions-belt.belt.no_potions"), true);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUNDLE_INSERT_FAIL, SoundSource.PLAYERS, 0.5F, 1.0F);
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
                // abort with the same feedback as an empty belt. Normally
                // caught earlier by onColumnSelected (which cuts the
                // animation short as soon as the column proves empty); this
                // is the fallback for the rare case where it became empty
                // between selection and finish.
                announceNoPotions(player, level);
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
