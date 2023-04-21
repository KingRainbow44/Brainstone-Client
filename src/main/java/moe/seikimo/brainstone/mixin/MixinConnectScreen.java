package moe.seikimo.brainstone.mixin;

import moe.seikimo.brainstone.client.events.JoinServerEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public abstract class MixinConnectScreen {
    @Inject(method = "connect", at = @At("HEAD"))
    public void connect(Minecraft minecraft, ServerAddress serverAddress, ServerData serverData, CallbackInfo ci) {
        if (serverData == null || serverData.isLan()) return;

        // Invoke the join server event.
        JoinServerEvent.EVENT.invoker().onJoinServer(minecraft.player, serverData);
    }
}
