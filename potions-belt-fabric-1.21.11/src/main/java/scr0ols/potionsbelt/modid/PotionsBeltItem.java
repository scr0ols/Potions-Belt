package scr0ols.potionsbelt.modid;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PotionsBeltItem extends Item {

    public PotionsBeltItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Shift+right-click → abre GUI completa (a implementar depois)
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                // TODO: abrir PotionsBeltScreenHandler
            }
            return InteractionResult.SUCCESS;
        }

        // Right-click normal → ativa mini HUD (a implementar depois)
        if (level.isClientSide()) {
            // TODO: ativar mini HUD
        }

        return InteractionResult.SUCCESS;
    }
}
