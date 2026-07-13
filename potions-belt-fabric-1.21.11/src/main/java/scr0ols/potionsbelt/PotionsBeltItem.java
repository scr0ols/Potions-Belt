package scr0ols.potionsbelt;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class PotionsBeltItem extends Item {

    public PotionsBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // Sneak + right click -> open the belt menu (milestone 3).
        // Plain right click -> drink the first available potion (milestone 4).
        return InteractionResult.SUCCESS;
    }
}
