package me.kimovoid.modernbetacompanion.mixin.cape;

import me.kimovoid.modernbetacompanion.ModernBetaCompanion;
import me.kimovoid.modernbetacompanion.cape.CapeService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {

    @Shadow private Minecraft minecraft;

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void onServerJoin(CallbackInfo ci) {
        if (!ModernBetaCompanion.isPlayingModernBeta()) return;

        /* Causes a small lag spike when joining the server but it's no big deal IMO */
        CapeService.INSTANCE.init(this.minecraft.player, null);
    }
}