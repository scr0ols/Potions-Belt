package scr0ols.potionsbelt;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PotionsBeltItem extends Item {

    public PotionsBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack belt = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                player.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, p) ->
                                new PotionsBeltMenu(containerId, playerInventory,
                                        BeltInventory.load(belt), belt),
                        belt.getHoverName()));
            }
            return InteractionResult.SUCCESS;
        }
        // Plain right click -> drink the first available potion (milestone 4).
        return InteractionResult.PASS;
    }
}
