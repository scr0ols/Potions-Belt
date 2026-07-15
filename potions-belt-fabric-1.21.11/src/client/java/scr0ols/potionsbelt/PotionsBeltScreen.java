package scr0ols.potionsbelt;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PotionsBeltScreen extends AbstractContainerScreen<PotionsBeltMenu> {

    // Placeholder: the vanilla shulker box GUI is exactly 3x9, custom art comes later.
    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/shulker_box.png");

    public PotionsBeltScreen(PotionsBeltMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        // the shulker box texture is one pixel taller than the default 166
        ++this.imageHeight;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);
    }
}
