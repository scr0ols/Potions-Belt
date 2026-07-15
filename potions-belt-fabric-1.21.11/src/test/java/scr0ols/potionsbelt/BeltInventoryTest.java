package scr0ols.potionsbelt;

import java.util.Map;

import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.ItemContainerContents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeltInventoryTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    /** Belt = any stack with a CONTAINER component; slot index -> content. */
    private static ItemStack belt(Map<Integer, ItemStack> slots) {
        NonNullList<ItemStack> items = NonNullList.withSize(BeltInventory.SIZE, ItemStack.EMPTY);
        slots.forEach(items::set);
        ItemStack belt = new ItemStack(Items.STICK);
        belt.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        return belt;
    }

    private static ItemStack potion(Holder<Potion> type) {
        return PotionContents.createItemStack(Items.POTION, type);
    }

    private static ItemStack bottle() {
        return new ItemStack(Items.GLASS_BOTTLE);
    }

    private static NonNullList<ItemStack> contents(ItemStack belt) {
        NonNullList<ItemStack> items = NonNullList.withSize(BeltInventory.SIZE, ItemStack.EMPTY);
        belt.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
        return items;
    }

    private static Holder<Potion> potionType(ItemStack stack) {
        return stack.get(DataComponents.POTION_CONTENTS).potion().orElseThrow();
    }

    @Test
    void gappyBeltReplacesOnlyTheDrunkSlot() {
        // row-major: bottle at 2, potions at 5, 13, 20, gaps elsewhere
        ItemStack belt = belt(Map.of(
                2, bottle(),
                5, potion(Potions.HEALING),
                13, potion(Potions.SWIFTNESS),
                20, potion(Potions.LEAPING)));

        int slot = BeltInventory.firstPotionSlot(belt);
        assertEquals(5, slot);
        ItemStack taken = BeltInventory.takePotionAt(belt, slot, false);

        assertEquals(Potions.HEALING, potionType(taken));
        NonNullList<ItemStack> after = contents(belt);
        assertTrue(after.get(2).is(Items.GLASS_BOTTLE)); // untouched pre-existing bottle
        assertTrue(after.get(5).is(Items.GLASS_BOTTLE)); // drunk slot became a bottle
        assertEquals(Potions.SWIFTNESS, potionType(after.get(13))); // untouched, same slot
        assertEquals(Potions.LEAPING, potionType(after.get(20))); // untouched, same slot
        for (int i = 0; i < BeltInventory.SIZE; i++) {
            if (i == 2 || i == 5 || i == 13 || i == 20) {
                continue;
            }
            assertTrue(after.get(i).isEmpty(), "slot " + i + " should be empty");
        }
    }

    @Test
    void fullBeltReplacesOnlyFirstSlot() {
        NonNullList<ItemStack> items = NonNullList.withSize(BeltInventory.SIZE, ItemStack.EMPTY);
        for (int i = 0; i < BeltInventory.SIZE; i++) {
            items.set(i, potion(i == 0 ? Potions.HEALING : Potions.SWIFTNESS));
        }
        ItemStack belt = new ItemStack(Items.STICK);
        belt.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));

        int slot = BeltInventory.firstPotionSlot(belt);
        assertEquals(0, slot);
        ItemStack taken = BeltInventory.takePotionAt(belt, slot, false);

        assertEquals(Potions.HEALING, potionType(taken));
        NonNullList<ItemStack> after = contents(belt);
        assertTrue(after.get(0).is(Items.GLASS_BOTTLE));
        for (int i = 1; i < BeltInventory.SIZE; i++) {
            assertEquals(Potions.SWIFTNESS, potionType(after.get(i)));
        }
    }

    @Test
    void bottlesOnlyBeltYieldsNothingAndIsUntouched() {
        ItemStack belt = belt(Map.of(0, bottle(), 7, bottle()));
        ItemContainerContents before = belt.get(DataComponents.CONTAINER);

        assertEquals(-1, BeltInventory.firstPotionSlot(belt));
        assertFalse(BeltInventory.hasPotion(belt));
        assertEquals(before, belt.get(DataComponents.CONTAINER));
    }

    @Test
    void emptyBeltYieldsNothing() {
        ItemStack belt = new ItemStack(Items.STICK);
        assertEquals(-1, BeltInventory.firstPotionSlot(belt));
        assertFalse(BeltInventory.hasPotion(belt));
    }

    @Test
    void creativeLeavesTheBeltEntirelyUntouched() {
        ItemStack belt = belt(Map.of(0, potion(Potions.HEALING), 1, bottle()));
        ItemContainerContents before = belt.get(DataComponents.CONTAINER);

        int slot = BeltInventory.firstPotionSlot(belt);
        ItemStack taken = BeltInventory.takePotionAt(belt, slot, true);

        assertEquals(Potions.HEALING, potionType(taken));
        // Nothing written back at all - matches vanilla creative drinking.
        assertEquals(before, belt.get(DataComponents.CONTAINER));
    }

    @Test
    void hasPotionSeesPotionsButNotBottles() {
        assertTrue(BeltInventory.hasPotion(belt(Map.of(26, potion(Potions.HEALING)))));
        assertFalse(BeltInventory.hasPotion(belt(Map.of(0, bottle()))));
    }

    @Test
    void columnWithPotionInRow1PicksItDirectly() {
        // column 3 (index 2): row1 = potion, row2/row3 = other potions
        ItemStack belt = belt(Map.of(
                2, potion(Potions.HEALING),
                11, potion(Potions.SWIFTNESS),
                20, potion(Potions.LEAPING)));

        int slot = BeltInventory.firstPotionSlotInColumn(belt, 3);

        assertEquals(2, slot);
        assertEquals(Potions.HEALING, potionType(contents(belt).get(slot)));
    }

    @Test
    void columnFallsBackToRow2WhenRow1EmptyOrBottle() {
        // column 5 (index 4): row1 = bottle, row2 = potion
        ItemStack belt = belt(Map.of(
                4, bottle(),
                13, potion(Potions.SWIFTNESS)));

        int slot = BeltInventory.firstPotionSlotInColumn(belt, 5);

        assertEquals(13, slot);
        assertEquals(Potions.SWIFTNESS, potionType(contents(belt).get(slot)));
    }

    @Test
    void columnFallsBackToRow3WhenRow1AndRow2EmptyOrBottle() {
        // column 9 (index 8): row1 empty, row2 = bottle, row3 = potion
        ItemStack belt = belt(Map.of(
                17, bottle(),
                26, potion(Potions.LEAPING)));

        int slot = BeltInventory.firstPotionSlotInColumn(belt, 9);

        assertEquals(26, slot);
        assertEquals(Potions.LEAPING, potionType(contents(belt).get(slot)));
    }

    @Test
    void columnEmptyInAllRowsReturnsNoSlot() {
        // column 1 (index 0): only a bottle across all 3 rows, potions live elsewhere
        ItemStack belt = belt(Map.of(
                0, bottle(),
                1, potion(Potions.HEALING)));

        assertEquals(-1, BeltInventory.firstPotionSlotInColumn(belt, 1));
    }

    @Test
    void columnOutOfRangeReturnsNoSlot() {
        ItemStack belt = belt(Map.of(0, potion(Potions.HEALING)));

        assertEquals(-1, BeltInventory.firstPotionSlotInColumn(belt, 0));
        assertEquals(-1, BeltInventory.firstPotionSlotInColumn(belt, 10));
    }
}
