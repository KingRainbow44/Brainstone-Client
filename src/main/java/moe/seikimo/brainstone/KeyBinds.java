package moe.seikimo.brainstone;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;

/** Mod key bindings. */
public interface KeyBinds {
    /**
     * Key press handler.
     */
    interface Handler {
        /**
         * Invoked when the key is pressed.
         */
        void handle();
    }

    KeyMapping PANIC_MODE = new KeyMapping(
            "key.brainstone.panic",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            "category.brainstone.controls"
    );

    KeyMapping DISARM = new KeyMapping(
            "key.brainstone.disarm",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_INSERT,
            "category.brainstone.controls"
    );

    KeyMapping STASIS_PRIMARY = new KeyMapping(
            "key.brainstone.stasis_1",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_J,
            "category.brainstone.controls"
    );

    KeyMapping STASIS_SECONDARY = new KeyMapping(
            "key.brainstone.stasis_2",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "category.brainstone.controls"
    );

    List<KeyMapping> ALL = List.of(
            PANIC_MODE,
            DISARM,
            STASIS_PRIMARY,
            STASIS_SECONDARY
    );

    Map<KeyMapping, Handler> HANDLERS = Map.of(
            PANIC_MODE, Brainstone::panicMode,
            DISARM, Brainstone::disarm,
            STASIS_PRIMARY, () -> Brainstone.triggerStasis(true),
            STASIS_SECONDARY, () -> Brainstone.triggerStasis(false)
    );
}
