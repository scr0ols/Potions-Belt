package scr0ols.potionsbelt;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final SoundEvent BELT_OPEN = register("belt_open");
    public static final SoundEvent BOTTLE_OPEN = register("bottle_open");
    public static final SoundEvent BOTTLE_CLOSE = register("bottle_close");

    private static SoundEvent register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(PotionsBelt.MOD_ID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void initialize() {
        // Registration happens via static initializers above; this just
        // forces the class to load from PotionsBelt.onInitialize().
    }
}
