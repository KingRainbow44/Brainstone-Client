package moe.seikimo.brainstone.mixin;

import moe.seikimo.brainstone.client.events.PlayerMoveEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {
    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {
        super.move(moverType, vec3);

        // Invoke the player move event.
        PlayerMoveEvent.EVENT.invoker()
                .onPlayerMove((Player) (Object) this, this.position());
    }
}
