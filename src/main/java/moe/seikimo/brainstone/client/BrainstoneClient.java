package moe.seikimo.brainstone.client;

import lombok.Getter;
import moe.seikimo.brainstone.Brain;
import moe.seikimo.brainstone.Brainstone;
import moe.seikimo.brainstone.KeyBinds;
import moe.seikimo.brainstone.client.events.JoinServerEvent;
import moe.seikimo.brainstone.client.events.PlayerMoveEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

public final class BrainstoneClient implements ClientModInitializer {
    @Getter private static final List<Vec3> queuedPositions
            = Collections.synchronizedList(new ArrayList<>());

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        Brainstone.getLogger().info("Brainstone client is initializing...");

        // Register key binds.
        KeyBinds.ALL.forEach(KeyBindingHelper::registerKeyBinding);
        // Register event handlers.
        ClientTickEvents.END_CLIENT_TICK.register(this::checkKeyBinds);
        PlayerMoveEvent.EVENT.register(this::onPlayerMove);
        JoinServerEvent.EVENT.register(this::onJoinServer);
    }

    /**
     * Invoked when the player moves.
     */
    private void onPlayerMove(Player player, Vec3 position) {
        // Check if the player is moving.
        if (player.getDeltaMovement().length() == 0) return;

        new Thread(() -> {
            try {
                // Check if the player is connected.
                if (!Brainstone.connectedToServer()) return;
                // Open doors which the player is close to.
                Brainstone.openNearbyDoors(player.level.dimension(), position);
            } catch (ConcurrentModificationException ignored) {

            }
        }).start();
    }

    /**
     * Invoked when the player joins a server.
     */
    private void onJoinServer(Player player, ServerData server) {
        // Check if the server is a Brainstone server.
        if (!Brainstone.isBrainstoneServer(server)) return;

        // Create a worker thread.
        new Thread(() -> {
            Brain.loadDoors(); // Load doors from the server.
        }).start();
    }

    /**
     * Checks if any key binds are pressed.
     */
    private void checkKeyBinds(Minecraft client) {
        KeyBinds.ALL.forEach(keyBind -> {
            if (keyBind.isDown()) {
                keyBind.setDown(false); // Prevent the key bind from being invoked repeatedly.

                // Invoke the key bind handler.
                new Thread(() -> KeyBinds.HANDLERS.get(keyBind).handle()).start();
            }
        });
    }
}
