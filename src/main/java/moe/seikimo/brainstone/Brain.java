package moe.seikimo.brainstone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import moe.seikimo.brainstone.backend.Door;
import moe.seikimo.brainstone.client.BrainstoneConfig;
import okhttp3.Request;

import java.io.IOException;
import java.util.List;

/** API wrapper for the brain. */
public interface Brain {
    String STASIS_CHAMBER = "user/%s/stasis/%s/activate";
    String FETCH_DOORS = "user/%s/door/all";
    String DOOR_ACTION = "user/%s/door/%s/%s";

    /**
     * Prepares an HTTP request to the brain.
     *
     * @param path The path to the endpoint.
     * @return The request.
     */
    static Request prepare(String path) {
        var config = BrainstoneConfig.get();
        return new Request.Builder().get()
                .url(config.getBaseUrl() + "/" + path)
                .build();
    }

    /**
     * Loads all doors from the backend.
     */
    static void loadDoors() {
        var config = BrainstoneConfig.get();

        // Prepare the request.
        // Try-with-resources will automatically close the response.
        try (var response = Brainstone.getHttpClient().newCall(
                Brain.prepare(FETCH_DOORS.formatted(config.getUserId()))
        ).execute()) {
            // Check if the request was successful.
            if (response.code() != Result.SUCCESS.getCode()) {
                Brainstone.getLogger().error("Failed to fetch doors.");
                return;
            }

            // Load the response body.
            var body = response.body();
            if (body == null) {
                Brainstone.getLogger().error("Failed to fetch doors.");
                return;
            }

            // Parse the response body.
            var parsed = Brainstone.getGson().fromJson(body.string(), Door[].class);
            if (parsed == null) {
                Brainstone.getLogger().error("Failed to fetch doors.");
                return;
            }

            // Clear the loaded doors.
            Brainstone.getLoadedDoors().clear();
            // Add the doors to the list.
            Brainstone.getLoadedDoors().addAll(List.of(parsed));
        } catch (IOException exception) {
            Brainstone.getLogger().error("Failed to perform request.", exception);
        }
    }

    /**
     * Triggers a stasis chamber.
     *
     * @param primary The stasis chamber type.
     */
    static Result triggerStasisChamber(boolean primary) {
        var config = BrainstoneConfig.get();

        // Prepare the request.
        // Try-with-resources will automatically close the response.
        try (var response = Brainstone.getHttpClient().newCall(
                Brain.prepare(STASIS_CHAMBER.formatted(
                        config.getUserId(), primary ?
                                config.getStasis().getPrimary() :
                                config.getStasis().getSecondary()
                ))
        ).execute()) {
            return response.code() == Result.SUCCESS.getCode() ?
                    Result.SUCCESS : Result.FAILURE;
        } catch (IOException exception) {
            Brainstone.getLogger().error("Failed to perform request.", exception);
            return Result.FAILURE;
        }
    }

    /**
     * Toggles the state of a door.
     *
     * @param door The door to open/close.
     * @param open True if the door should be opened, false if it should be closed.
     * @return The result of the request.
     */
    static Result toggleDoor(Door door, boolean open) {
        var config = BrainstoneConfig.get();

        // Prepare the request.
        // Try-with-resources will automatically close the response.
        try (var response = Brainstone.getHttpClient().newCall(
                Brain.prepare(DOOR_ACTION.formatted(
                        config.getUserId(), door.getDoorId(),
                        open ? "open" : "close"
                ))
        ).execute()) {
            return response.code() == Result.SUCCESS.getCode() ?
                    Result.SUCCESS : Result.FAILURE;
        } catch (IOException exception) {
            Brainstone.getLogger().error("Failed to perform request.", exception);
            return Result.FAILURE;
        }
    }

    @AllArgsConstructor
    @Getter enum Result {
        SUCCESS(200),
        FAILURE(404);

        private final int code;
    }
}
