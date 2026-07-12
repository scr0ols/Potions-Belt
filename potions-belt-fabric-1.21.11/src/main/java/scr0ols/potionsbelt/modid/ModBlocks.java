package scr0ols.potionsbelt.modid;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {

    public static final Block POTION_SHELF = registerBlock("potion_shelf",
            BlockBehaviour.Properties.of().strength(2.0f));

    private static Block registerBlock(String name, BlockBehaviour.Properties properties) {
        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK,
                Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, name));
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, name));

        Block block = new Block(properties.setId(blockKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey,
                new BlockItem(block, new Item.Properties().setId(itemKey)));

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.accept(POTION_SHELF);
        });

        PotionsBelt.LOGGER.info("ModBlocks inicializado!");
    }
}
