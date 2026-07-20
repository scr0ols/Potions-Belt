package com.scr0ols.potionsbelt;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {

    private static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, PotionsBelt.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> BELT_OPEN = register("belt_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOTTLE_OPEN = register("bottle_open");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOTTLE_CLOSE = register("bottle_close");

    private ModSounds() {
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(
                Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, name)));
    }

    public static void initialize(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }
}
