package moe.seikimo.brainstone;

import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import moe.seikimo.brainstone.backend.Door;
import moe.seikimo.brainstone.client.BrainstoneConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Brainstone implements ModInitializer {
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Brainstone");
    @Getter private static final OkHttpClient httpClient
            = new OkHttpClient();

    @Getter private static final List<Door> loadedDoors
            = Collections.synchronizedList(new ArrayList<>());
    @Getter private static final List<Door> openedDoors
            = Collections.synchronizedList(new ArrayList<>());
    @Getter private static BrainstoneConfig config;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        Brainstone.getLogger().info("Brainstone is initializing...");

        // Register the auto-config serializer.
        AutoConfig.register(BrainstoneConfig.class, GsonConfigSerializer::new);
        // Set the config.
        Brainstone.config = AutoConfig.getConfigHolder(BrainstoneConfig.class).getConfig();
    }

    /**
     * Checks if the player is connected to the server.
     *
     * @return True if the player is connected to the server, false otherwise.
     */
    public static boolean connectedToServer() {
        var player = Minecraft.getInstance().player;
        if (player == null) return false;

        var connection = player.connection;
        var server = connection.getServerData();
        if (server == null) return false;

        return server.ip.contains("mc.magix.lol") ||
                server.ip.contains("192.168.198");
    }

    /**
     * Sets the base into panic mode.
     */
    public static void panicMode() {
        // Check if the player is on the correct server.
        if (!Brainstone.connectedToServer()) return;
    }

    /**
     * Disarms the security system of the base.
     */
    public static void disarm() {
        // Check if the player is on the correct server.
        if (!Brainstone.connectedToServer()) return;
    }

    /**
     * Triggers a stasis chamber.
     *
     * @param primary The stasis chamber type.
     */
    public static void triggerStasis(boolean primary) {
        // Check if the player is on the correct server.
        if (!Brainstone.connectedToServer()) return;

        var client = Minecraft.getInstance();
        var player = client.player;

        // Check if the player is in-game.
        if (player == null) return;

        // Trigger the stasis chamber.
        var result = Brain.triggerStasisChamber(primary);
        var success = result == Brain.Result.SUCCESS;
        // Send an action bar message to the player.
        client.execute(() -> client.player.displayClientMessage(
                Component.translatable(success ?
                        "action.brainstone.stasis.success" : "action.brainstone.stasis.failure")
                        .withStyle(Style.EMPTY.withColor(success ?
                                ChatFormatting.GREEN : ChatFormatting.RED)),
                true));
    }

    /**
     * Opens all nearby doors.
     *
     * @param world The world to open the doors in.
     * @param position The position to open the doors around.
     */
    public static void openNearbyDoors(ResourceKey<Level> world, Vec3 position) {
        var config = Brainstone.getConfig();

        // Get all valid doors.
        var doors = Brainstone.getLoadedDoors().stream()
                // Find doors in the same dimension.
                .filter(door -> door.getDimension().equals(world))
                .filter(door -> {
                    var doorPos = door.toPosition();
                    return doorPos.distanceTo(position) <= config.getSecurity().getAutoDoorDistance();
                })
                .sorted((doorA, doorB) -> {
                    var a = doorA.toPosition();
                    var b = doorB.toPosition();
                    return (int) (a.distanceTo(position) - b.distanceTo(position));
                })
                .toList();

        // Find doors that are in the open map which don't exist in the valid doors list.
        // Close these doors afterwards.
        Brainstone.getOpenedDoors().stream()
                .filter(door -> !doors.contains(door))
                .forEach(door -> {
                    // Close the door.
                    var result = Brain.toggleDoor(door, false);
                    if (result == Brain.Result.SUCCESS) {
                        Brainstone.getOpenedDoors().remove(door);
                    }
                });

        // Open all doors.
        doors.forEach(door -> {
            // Check if the door is already open.
            if (Brainstone.getOpenedDoors().contains(door)) return;

            // Open the door.
            var result = Brain.toggleDoor(door, true);
            if (result == Brain.Result.SUCCESS) {
                Brainstone.getOpenedDoors().add(door);
            }
        });
    }
}
