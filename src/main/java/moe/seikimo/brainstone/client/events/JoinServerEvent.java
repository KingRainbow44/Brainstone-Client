package moe.seikimo.brainstone.client.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.entity.player.Player;

/** Player movement event. */
public interface JoinServerEvent {
    Event<JoinServerEvent> EVENT = EventFactory.createArrayBacked(JoinServerEvent.class,
            (listeners) -> (player, server) -> {
                for (var listener : listeners) {
                    listener.onJoinServer(player, server);
                }
            });

    /**
     * @param player The player reference.
     * @param server The server joined.
     */
    void onJoinServer(Player player, ServerData server);
}
