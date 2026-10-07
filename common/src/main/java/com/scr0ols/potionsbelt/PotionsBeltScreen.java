package com.scr0ols.potionsbelt;

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
        // the shulker box texture is one pixel taller than the default 166; imageWidth/imageHeight
        // are final on 26.2, set via this constructor overload instead of mutated after the fact
        super(menu, playerInventory, title, 176, 167);
    }

    // 26.2's Screen render pipeline replaced render()/renderBg() with an extract*() state-snapshot
    // API (GuiGraphicsExtractor); AbstractContainerScreen.extractRenderState already calls
    // extractTooltip on its own, so only the background needs overriding here (same pattern
    // ShulkerBoxScreen itself uses, verified in the decompiled 26.2 source).
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0.0F, 0.0F,
                this.imageWidth, this.imageHeight, 256, 256);
    }
}
