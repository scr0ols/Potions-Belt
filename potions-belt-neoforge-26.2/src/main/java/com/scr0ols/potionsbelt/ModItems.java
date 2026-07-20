package com.scr0ols.potionsbelt;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PotionsBelt.MOD_ID);

    public static final DeferredItem<PotionsBeltItem> POTIONS_BELT = ITEMS.registerItem(
            "potions_belt",
            PotionsBeltItem::new,
            () -> new Item.Properties()
                    .stacksTo(1)
                    // Gives the belt the vanilla drink use (32 ticks, DRINK animation,
                    // drink sounds). PotionsBeltItem overrides use/finishUsingItem, so
                    // the belt itself is never consumed.
                    .component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK));

    private ModItems() {
    }

    public static void initialize(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(ModItems::addToCreativeTab);
    }

    private static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(POTIONS_BELT);
        }
    }
}
