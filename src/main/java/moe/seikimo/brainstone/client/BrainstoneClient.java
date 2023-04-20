package moe.seikimo.brainstone.client;

import moe.seikimo.brainstone.Brainstone;
import moe.seikimo.brainstone.KeyBinds;
import moe.seikimo.brainstone.client.events.PlayerMoveEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class BrainstoneClient implements ClientModInitializer {
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
    }

    /**
     * Invoked when the player moves.
     */
    private void onPlayerMove(Player player, Vec3 position) {
        // Check if the player is connected.
        if (!Brainstone.connectedToServer()) return;
        // Check if the player is moving.
        if (player.getDeltaMovement().length() == 0) return;

        // Open doors which the player is close to.
        Brainstone.openNearbyDoors(player.level.dimension(), position);
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
