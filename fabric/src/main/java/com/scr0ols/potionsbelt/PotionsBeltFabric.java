package com.scr0ols.potionsbelt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.item.CreativeModeTabs;

public class PotionsBeltFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entries -> entries.accept(ModItems.POTIONS_BELT.get()));

        PayloadTypeRegistry.serverboundPlay().register(SelectColumnPayload.TYPE, SelectColumnPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SelectColumnPayload.TYPE,
                (payload, context) -> PotionsBeltItem.onColumnSelected(context.player(), payload.column()));
        PayloadTypeRegistry.serverboundPlay().register(OpenBeltMenuPayload.TYPE, OpenBeltMenuPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(OpenBeltMenuPayload.TYPE,
                (payload, context) -> PotionsBeltItem.openMenu(context.player()));
        ServerPlayConnectionEvents.DISCONNECT.register(
                (handler, server) -> BeltSelections.clear(handler.getPlayer()));
        ServerTickEvents.END_SERVER_TICK.register(server -> DelayedBottleClose.tick());

        Constants.LOG.info("Potion's Belt initialized on Fabric");
    }
}
