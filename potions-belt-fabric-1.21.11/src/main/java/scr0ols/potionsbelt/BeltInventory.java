package scr0ols.potionsbelt;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;

/** Reads and writes the belt's contents, stored in DataComponents.CONTAINER. */
public final class BeltInventory {

    public static final int SIZE = 27;

    private BeltInventory() {
    }

    public static SimpleContainer load(ItemStack belt) {
        ItemContainerContents contents =
                belt.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        contents.copyInto(items);
        SimpleContainer container = new SimpleContainer(SIZE);
        for (int i = 0; i < SIZE; i++) {
            container.setItem(i, items.get(i));
        }
        return container;
    }

    public static void save(ItemStack belt, Container container) {
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        // copies, so later in-place mutations of the live slots can't alias the component
        for (int i = 0; i < SIZE; i++) {
            items.set(i, container.getItem(i).copy());
        }
        belt.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    /** Drinkable potions only; splash/lingering are different items and rejected (v1 decision). */
    public static boolean isDrinkablePotion(ItemStack stack) {
        return stack.is(Items.POTION);
    }

    /** What the belt slots accept: drinkable potions, plus empty bottles (the drink byproduct). */
    public static boolean isAcceptable(ItemStack stack) {
        return isDrinkablePotion(stack) || stack.is(Items.GLASS_BOTTLE);
    }

    /** Row-major index (0..SIZE-1) of the first drinkable potion, or -1 if none. */
    public static int firstPotionSlot(ItemStack belt) {
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        belt.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        for (int i = 0; i < SIZE; i++) {
            if (isDrinkablePotion(items.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /** True if the belt holds at least one drinkable potion (bottles don't count). */
    public static boolean hasPotion(ItemStack belt) {
        return firstPotionSlot(belt) >= 0;
    }

    /**
     * Removes the potion at the given slot, in place: every other slot is
     * left exactly as it was (no compaction/shifting), preserving whatever
     * column layout the player set up. If keepPotion is true (creative),
     * nothing is written back at all, matching vanilla creative drinking
     * (the item is never actually consumed). Returns the potion that was at
     * that slot, or ItemStack.EMPTY if it didn't hold one.
     */
    public static ItemStack takePotionAt(ItemStack belt, int slot, boolean keepPotion) {
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        belt.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);

        ItemStack potion = items.get(slot);
        if (!isDrinkablePotion(potion)) {
            return ItemStack.EMPTY;
        }
        if (!keepPotion) {
            items.set(slot, new ItemStack(Items.GLASS_BOTTLE));
            belt.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        }
        return potion;
    }
}
