package com.scr0ols.potionsbelt;

import com.scr0ols.potionsbelt.platform.Services;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;

import java.util.function.Supplier;

public class ModItems {

    public static final Supplier<Item> POTIONS_BELT = register("potions_belt");

    private ModItems() {
    }

    private static Supplier<Item> register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
        return Services.REGISTRY.register(BuiltInRegistries.ITEM, id,
                () -> new PotionsBeltItem(new Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, id))
                        .stacksTo(1)
                        // Gives the belt the vanilla drink use (32 ticks, DRINK animation,
                        // drink sounds). PotionsBeltItem overrides use/finishUsingItem, so
                        // the belt itself is never consumed.
                        .component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK)));
    }

    /** Forces the class to load, which queues the registration above. */
    public static void init() {
    }
}
