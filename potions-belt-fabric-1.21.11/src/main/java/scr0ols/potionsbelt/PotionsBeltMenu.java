package scr0ols.potionsbelt;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** 27 potion-only slots (3x9), same layout as the vanilla shulker box menu. */
public class PotionsBeltMenu extends AbstractContainerMenu {

    private static final int BELT_SLOTS = BeltInventory.SIZE;

    private final Container container;
    private final ItemStack beltStack;

    /** Client constructor: slot contents arrive via vanilla menu sync. */
    public PotionsBeltMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(BELT_SLOTS), ItemStack.EMPTY);
    }

    public PotionsBeltMenu(int containerId, Inventory playerInventory,
                           SimpleContainer container, ItemStack beltStack) {
        super(PotionsBelt.POTIONS_BELT_MENU, containerId);
        this.container = container;
        this.beltStack = beltStack;
        if (!beltStack.isEmpty()) {
            container.addListener(changed -> BeltInventory.save(beltStack, changed));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new PotionSlot(container, col + row * 9, 8 + col * 18, 18 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index < BELT_SLOTS) {
                if (!moveItemStackTo(stack, BELT_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, BELT_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!beltStack.isEmpty()) {
            BeltInventory.save(beltStack, container);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /** Belt grid slot: accepts drinkable potions only (moveItemStackTo also honors this). */
    private static class PotionSlot extends Slot {
        PotionSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return BeltInventory.isAcceptable(stack);
        }
    }
}
