package moe.seikimo.brainstone.backend;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

@Getter public final class Door {
    @SerializedName("dimension")
    private String dimensionKey;
    @SerializedName("id")
    private String doorId;
    private float x, y, z;

    private transient UUID id;
    private transient ResourceKey<Level> dimension;

    /**
     * Registers the dimension associated with this door position.
     * Sets the UUID of the door.
     */
    public void onLoad() {
        switch (this.dimensionKey) {
            default -> throw new IllegalArgumentException("Invalid dimension: " + this.dimensionKey);
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
        return new Vec3(this.x, this.y, this.z);
    }
}
