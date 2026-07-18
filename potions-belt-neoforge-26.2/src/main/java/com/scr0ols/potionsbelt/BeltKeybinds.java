package com.scr0ols.potionsbelt;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

/**
 * Dedicated, remappable keybindings for the belt: opening its menu, and a
 * modifier key that turns the vanilla hotbar keys 1-9 into column pickers
 * while held (see KeybindsMixin, which does the actual hotbar-key draining
 * -- it has to happen inside Minecraft#handleKeybinds, before vanilla's own
 * hotbar-switch loop later in that same method, or the switch happens
 * anyway). Both ship unbound by default -- see Controls > Potions Belt --
 * since a hardcoded default risks colliding with whatever a given player
 * already uses for movement or other mods.
 *
 * Column selection isn't its own 9 separate keybinds (tried first, see
 * NOTES.md 2026-07-14): binding those to the bare 1-9 keys collided with
 * vanilla's own hotbar-slot keybindings sharing the same physical keys --
 * pressing "3" both switched the hotbar to slot 3 *and* queued a column-3
 * click, and if slot 3 wasn't the belt, the belt was no longer the held
 * item by the time the column click was processed. Gating hotbar-key
 * reinterpretation behind a modifier avoids the collision entirely, since
 * the modifier is a physically distinct key from 1-9.
 */
@EventBusSubscriber(modid = PotionsBelt.MOD_ID, value = Dist.CLIENT)
public final class BeltKeybinds {

    // KeyMapping.Category.register(Identifier) is deprecated in favor of constructing the
    // record directly and registering it via RegisterKeyMappingsEvent#registerCategory below.
    private static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, "main"));

    private static final KeyMapping OPEN_MENU = create("open_menu");
    public static final KeyMapping SELECT_MODIFIER = create("select_modifier");

    private BeltKeybinds() {
    }

    private static KeyMapping create(String name) {
        return new KeyMapping("key.potionsbelt." + name,
                InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), CATEGORY);
    }

    @SubscribeEvent
    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(OPEN_MENU);
        event.register(SELECT_MODIFIER);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !PotionsBeltItem.isHeldBy(player)) {
            OPEN_MENU.consumeClick();
            return;
        }
        while (OPEN_MENU.consumeClick()) {
            ClientPacketDistributor.sendToServer(new OpenBeltMenuPayload());
        }
    }
}
