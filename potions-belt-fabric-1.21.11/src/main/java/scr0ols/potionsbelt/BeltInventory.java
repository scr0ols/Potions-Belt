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
}
