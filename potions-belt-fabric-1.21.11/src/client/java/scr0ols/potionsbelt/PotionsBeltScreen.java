package scr0ols.potionsbelt;

import net.minecraft.client.gui.GuiGraphicsExtractor;
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
        // the shulker box texture is one pixel taller than the default 166; imageHeight is
        // final now, so the +1 has to be passed in here instead of applied after super().
        super(menu, playerInventory, title, DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT + 1);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        extractTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);
        super.extractContents(guiGraphics, mouseX, mouseY, partialTick);
    }
}
