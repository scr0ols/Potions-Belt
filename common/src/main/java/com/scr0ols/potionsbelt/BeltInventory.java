package com.scr0ols.potionsbelt;

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
    public static final int COLUMNS = 9;
    public static final int ROWS = 3;

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

    /** Read-only lookup of the item at a given slot (0..SIZE-1), for HUD preview. */
    public static ItemStack getItem(ItemStack belt, int slot) {
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        belt.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        return items.get(slot);
    }

    /**
     * Row-major index of the first drinkable potion in the given 1-9 column,
     * checking row 1 -> row 2 -> row 3 (bottles are skipped, matching
     * firstPotionSlot). Returns -1 if the column is out of range or holds no
     * drinkable potion in any row.
     */
    public static int firstPotionSlotInColumn(ItemStack belt, int column) {
        if (column < 1 || column > COLUMNS) {
            return -1;
        }
        NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        belt.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        int col = column - 1;
        for (int row = 0; row < ROWS; row++) {
            int index = row * COLUMNS + col;
            if (isDrinkablePotion(items.get(index))) {
                return index;
            }
        }
        return -1;
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
