package scr0ols.potionsbelt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.world.entity.player.Player;

/**
 * Server-side per-player pending column selection (1-9) while drinking from
 * the belt. Fabric's networking API dispatches payload receivers on the
 * server thread, so a plain map is safe here.
 */
public final class BeltSelections {

    private static final Map<UUID, Integer> PENDING = new HashMap<>();

    private BeltSelections() {
    }

    public static void set(Player player, int column) {
        PENDING.put(player.getUUID(), column);
    }

    /** Pending column (1-9), or -1 if none is selected. */
    public static int get(Player player) {
        return PENDING.getOrDefault(player.getUUID(), -1);
    }

    public static void clear(Player player) {
        PENDING.remove(player.getUUID());
    }
}
