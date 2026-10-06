package com.scr0ols.potionsbelt;

import com.scr0ols.potionsbelt.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.function.Supplier;

public class ModSounds {

    public static final Supplier<SoundEvent> BELT_OPEN = register("belt_open");
    public static final Supplier<SoundEvent> BOTTLE_OPEN = register("bottle_open");
    public static final Supplier<SoundEvent> BOTTLE_CLOSE = register("bottle_close");

    private ModSounds() {
    }

    private static Supplier<SoundEvent> register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, name);
        return Services.REGISTRY.register(BuiltInRegistries.SOUND_EVENT, id,
                () -> SoundEvent.createVariableRangeEvent(id));
    }

    /** Forces the class to load, which queues the registrations above. */
    public static void init() {
    }
}
