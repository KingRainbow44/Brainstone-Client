package moe.seikimo.brainstone.client.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/** Player movement event. */
public interface PlayerMoveEvent {
    Event<PlayerMoveEvent> EVENT = EventFactory.createArrayBacked(PlayerMoveEvent.class,
            (listeners) -> (player, position) -> {
                for (var listener : listeners) {
                    listener.onPlayerMove(player, position);
                }
            });

    /**
     * @param player The player reference.
     * @param position The player's position.
     */
    void onPlayerMove(Player player, Vec3 position);
}
