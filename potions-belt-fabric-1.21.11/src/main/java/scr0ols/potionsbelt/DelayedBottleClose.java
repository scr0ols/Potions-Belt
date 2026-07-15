package scr0ols.potionsbelt;

import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Plays bottle_close for a drink -- either after a short delay (completed
 * drink, simulating lowering the bottle from mouth to hand before the cap
 * closes) or immediately (abandoned drink, via playImmediately). A single
 * drink can trigger this from more than one path in the same short window
 * (finishUsingItem's natural completion racing a same-moment release signal
 * from the client, or the hotbar-switch mixin) -- this dedupes per entity so
 * only the first trigger for a given drink actually plays the sound.
 *
 * Also tracks, per entity, how long the *next* drink should be held off for
 * (hasPending) -- this covers both the wait for the delayed close itself and
 * a short buffer afterward, so a chained drink's bottle_open never lands on
 * top of or immediately after the previous drink's bottle_close.
 */
public class DelayedBottleClose {
    private static final int DELAY_TICKS = 4; // ~0.2s, after a completed drink
    private static final int POST_CLOSE_GAP_TICKS = 3; // ~0.15s audible gap after bottle_close actually plays, before the next drink may start
    private static final int DEDUP_WINDOW_TICKS = 10; // ignore a second close trigger for the same entity this soon after one already claimed it

    private static int currentTick = 0;

    private static final Map<UUID, Integer> LAST_CLOSE_TICK = new HashMap<>();
    private static final Map<UUID, Integer> BLOCK_NEXT_DRINK_UNTIL = new HashMap<>();
    private static final List<Pending> PENDING = new ArrayList<>();

    private static final class Pending {
        final LivingEntity entity;
        final double x, y, z;
        int ticksLeft;

        Pending(LivingEntity entity, int ticksLeft) {
            this.entity = entity;
            this.x = entity.getX();
            this.y = entity.getY();
            this.z = entity.getZ();
            this.ticksLeft = ticksLeft;
        }
    }

    /** True while the next drink should still be held off for this entity (pending close, or its post-close gap). */
    public static boolean hasPending(LivingEntity entity) {
        Integer until = BLOCK_NEXT_DRINK_UNTIL.get(entity.getUUID());
        return until != null && currentTick < until;
    }

    /** Completed drink: play bottle_close a short beat after the vanilla drink sound. */
    public static void schedule(LivingEntity entity) {
        if (!claim(entity)) {
            return;
        }
        block(entity, DELAY_TICKS + POST_CLOSE_GAP_TICKS);
        PENDING.add(new Pending(entity, DELAY_TICKS));
    }

    /** Abandoned drink: play bottle_close immediately. */
    public static void playImmediately(LivingEntity entity) {
        if (!claim(entity)) {
            return;
        }
        block(entity, POST_CLOSE_GAP_TICKS);
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                ModSounds.BOTTLE_CLOSE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    /** True (and claims the window) if no other trigger has claimed this entity's close recently. */
    private static boolean claim(LivingEntity entity) {
        UUID id = entity.getUUID();
        Integer last = LAST_CLOSE_TICK.get(id);
        if (last != null && currentTick - last < DEDUP_WINDOW_TICKS) {
            return false;
        }
        LAST_CLOSE_TICK.put(id, currentTick);
        return true;
    }

    private static void block(LivingEntity entity, int ticks) {
        BLOCK_NEXT_DRINK_UNTIL.put(entity.getUUID(), currentTick + ticks);
    }

    public static void tick(MinecraftServer server) {
        currentTick++;
        Iterator<Pending> it = PENDING.iterator();
        while (it.hasNext()) {
            Pending pending = it.next();
            if (--pending.ticksLeft <= 0) {
                it.remove();
                pending.entity.level().playSound(null, pending.x, pending.y, pending.z,
                        ModSounds.BOTTLE_CLOSE, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
    }
}
