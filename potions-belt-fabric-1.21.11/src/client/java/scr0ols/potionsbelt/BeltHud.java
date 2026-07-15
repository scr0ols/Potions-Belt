package scr0ols.potionsbelt;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

/**
 * Renders a HUD icon (plus its name, always visible above the icon) for the
 * potion the player is about to drink, visible whenever the belt is held in
 * either hand (not just mid-drink). Placed like vanilla's offhand-item
 * hotbar indicator, but mirrored to the opposite side so it never overlaps
 * the real offhand icon.
 *
 * The preview mirrors PotionsBeltItem.finishUsingItem's slot resolution
 * exactly (default column, else first-available-anywhere, else empty) via
 * the same BeltInventory lookups the drink itself uses, so preview and
 * actual behavior can't drift apart.
 */
public final class BeltHud {

    private static final Identifier OFFHAND_LEFT_SPRITE = Identifier.withDefaultNamespace("hud/hotbar_offhand_left");
    private static final Identifier OFFHAND_RIGHT_SPRITE = Identifier.withDefaultNamespace("hud/hotbar_offhand_right");

    private BeltHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }

        ItemStack belt = heldBelt(player);
        if (belt.isEmpty()) {
            return;
        }

        int centerX = graphics.guiWidth() / 2;
        boolean onRight = player.getMainArm() == HumanoidArm.RIGHT;
        // Rendered on the same side as the main hand, i.e. the opposite side
        // from where vanilla draws the real offhand item indicator.
        int boxX = onRight ? centerX + 91 : centerX - 91 - 29;
        int boxY = graphics.guiHeight() - 23;
        int itemX = onRight ? centerX + 91 + 10 : centerX - 91 - 26;
        int itemY = graphics.guiHeight() - 19;

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, onRight ? OFFHAND_RIGHT_SPRITE : OFFHAND_LEFT_SPRITE,
                boxX, boxY, 29, 24);

        ItemStack preview = previewPotion(belt);
        if (!preview.isEmpty()) {
            graphics.renderItem(preview, itemX, itemY);
            graphics.renderItemDecorations(client.font, preview, itemX, itemY);
            graphics.drawCenteredString(client.font, preview.getHoverName(), boxX + 14, boxY - 10, 0xFFFFFF);
        }
    }

    private static ItemStack heldBelt(LocalPlayer player) {
        if (player.getMainHandItem().getItem() instanceof PotionsBeltItem) {
            return player.getMainHandItem();
        }
        if (player.getOffhandItem().getItem() instanceof PotionsBeltItem) {
            return player.getOffhandItem();
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack previewPotion(ItemStack belt) {
        int slot = BeltInventory.firstPotionSlotInColumn(belt, ClientBeltState.getDefaultColumn());
        if (slot < 0) {
            slot = BeltInventory.firstPotionSlot(belt);
        }
        return slot >= 0 ? BeltInventory.getItem(belt, slot) : ItemStack.EMPTY;
    }
}
