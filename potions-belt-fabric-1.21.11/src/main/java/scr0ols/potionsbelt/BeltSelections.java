package scr0ols.potionsbelt;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.world.entity.player.Player;

/**
 * Server-side per-player default belt column (1-9): sticky across drinks,
 * not just the one in progress. Used both to resolve a plain right click
 * (tried first, before falling back to "first potion anywhere") and to
 * remember explicit number-key picks made while drinking. Starts at column
 * 1 for a player who has never picked one; only cleared on disconnect (see
 * ServerPlayConnectionEvents.DISCONNECT in PotionsBelt). Fabric's networking
 * API dispatches payload receivers on the server thread, so a plain map is
 * safe here.
 */
public final class BeltSelections {

    private static final int DEFAULT_COLUMN = 1;

    private static final Map<UUID, Integer> DEFAULT_COLUMNS = new HashMap<>();

    private BeltSelections() {
    }

    public static void set(Player player, int column) {
        DEFAULT_COLUMNS.put(player.getUUID(), column);
    }

    /** The player's current default column (1-9); column 1 if never set. */
    public static int get(Player player) {
        return DEFAULT_COLUMNS.getOrDefault(player.getUUID(), DEFAULT_COLUMN);
    }

    public static void clear(Player player) {
        DEFAULT_COLUMNS.remove(player.getUUID());
    }
}
