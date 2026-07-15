package scr0ols.potionsbelt;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final Item POTIONS_BELT = registerItem("potions_belt",
            new PotionsBeltItem(new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM,
                            Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, "potions_belt")))
                    .stacksTo(1)));

    private static Item registerItem(String name, Item item) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(POTIONS_BELT);
        });

        PotionsBelt.LOGGER.info("ModItems initialized");
    }
}
