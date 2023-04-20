package moe.seikimo.brainstone.client;

import lombok.Getter;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.CollapsibleObject;
import moe.seikimo.brainstone.Brainstone;

@Config(name = "brainstone")
@Getter public final class BrainstoneConfig implements ConfigData {
    /**
     * Returns an instance of the config.
     *
     * @return The config.
     */
    public static BrainstoneConfig get() {
        return Brainstone.getConfig();
    }

    private String baseUrl = "http://192.168.0.198:25563";
    private String userId = "";

    @CollapsibleObject
    private Security security = new Security();

    @CollapsibleObject
    private StasisChambers stasis = new StasisChambers();

    @Getter
    public static class Security {
        private int disarmDuration = 10;
        private boolean autoOpenDoors = true;
        private int autoDoorDistance = 3;
    }

    @Getter
    public static class StasisChambers {
        private String primary = "";
        private String secondary = "";
    }
}
