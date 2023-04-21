package moe.seikimo.brainstone.backend;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

@Getter @ToString
public final class Door {
    @SerializedName("id")
    private String doorId;

    private DoorInfo info;
    private DoorPosition position;

    private transient UUID id;
    private transient ResourceKey<Level> dimension;

    /**
     * Represents the door info.
     */
    @Getter @ToString
    static class DoorInfo {
        private String name;
        private String description;
        private String key;
    }

    /**
     * Represents the door info.
     */
    @Getter @ToString
    static class DoorPosition {
        @SerializedName("dimension")
        private String dimensionKey;
        private float x, y, z;
    }

    /**
     * @return The X coordinate of the door.
     */
    public float getX() {
        return this.getPosition().getX();
    }

    /**
     * @return The Y coordinate of the door.
     */
    public float getY() {
        return this.getPosition().getY();
    }

    /**
     * @return The Z coordinate of the door.
     */
    public float getZ() {
        return this.getPosition().getZ();
    }

    /**
     * @return The dimension key of the door.
     */
    public String getDimensionKey() {
        return this.getPosition().getDimensionKey();
    }

    /**
     * Registers the dimension associated with this door position.
     * Sets the UUID of the door.
     */
    public void onLoad() {
        switch (this.getDimensionKey()) {
            default -> throw new IllegalArgumentException("Invalid dimension: " + this.getDimension());
            case "minecraft:overworld" -> this.dimension = Level.OVERWORLD;
            case "minecraft:the_nether" -> this.dimension = Level.NETHER;
            case "minecraft:the_end" -> this.dimension = Level.END;
        }

        this.id = UUID.fromString(this.doorId);
    }

    /**
     * Converts this door position to a Vec3.
     *
     * @return The Vec3 representation of this door position.
     */
    public Vec3 toPosition() {
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }
}
