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
        // Right click always just drinks now; opening the menu and picking a
        // column both live on their own dedicated keybinds (BeltKeybinds),
        // decoupled from right click entirely.
        ItemStack belt = player.getItemInHand(hand);
        if (BeltInventory.hasPotion(belt)) {
            // The belt's CONSUMABLE component (DEFAULT_DRINK) provides the
            // vanilla 1.6 s drink animation and sounds; the potion is only
            // consumed at finishUsingItem.
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

    /** True if the player is holding a belt in either hand. */
    public static boolean isHeldBy(Player player) {
        return player.getMainHandItem().getItem() instanceof PotionsBeltItem
                || player.getOffhandItem().getItem() instanceof PotionsBeltItem;
    }

    /**
     * Called server-side when the "Open Belt Menu" keybind is pressed. Mirrors
     * the menu-opening logic use() used to run on sneak+right-click, now fully
     * independent of right click. Main hand takes priority if both hands
     * somehow hold a belt.
     */
    public static void openMenu(Player player) {
        ItemStack belt = player.getMainHandItem().getItem() instanceof PotionsBeltItem
                ? player.getMainHandItem()
                : player.getOffhandItem();
        if (!(belt.getItem() instanceof PotionsBeltItem)) {
            return;
        }
        player.openMenu(new SimpleMenuProvider(
                (containerId, playerInventory, p) ->
                        new PotionsBeltMenu(containerId, playerInventory, BeltInventory.load(belt), belt),
                belt.getHoverName()));
    }

    /**
     * Called server-side when a SelectColumnPayload arrives. Records the
     * player's new default column (sticky: it stays selected for future
     * drinks too, not just the one in progress), then, if the player is
     * currently drinking from the belt and that column is already known to
     * be empty, cuts the drink animation short right away instead of making
     * the player wait for the full 1.6 s just to see the same "nothing to
     * drink" feedback at the end.
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
            // Try the player's default column first; if it's empty (or there
            // is no player), fall back to the first potion anywhere in the
            // belt instead of failing outright.
            int slot = player != null
                    ? BeltInventory.firstPotionSlotInColumn(belt, BeltSelections.get(player))
                    : -1;
            if (slot < 0) {
                slot = BeltInventory.firstPotionSlot(belt);
            }

            if (slot >= 0) {
                boolean keepPotion = player != null && player.hasInfiniteMaterials();
                ItemStack potion = BeltInventory.takePotionAt(belt, slot, keepPotion);
                Consumable consumable = potion.get(DataComponents.CONSUMABLE);
                if (consumable != null) {
                    // Applies the potion exactly like drinking it normally:
                    // effects, stats, advancement trigger, finish sounds.
                    consumable.onConsume(level, entity, potion);
                }
            } else if (player != null) {
                announceNoPotions(player, level);
            }
        }
        return belt;
    }
}
