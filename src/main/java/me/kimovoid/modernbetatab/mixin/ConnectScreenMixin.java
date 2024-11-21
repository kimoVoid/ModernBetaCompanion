package me.kimovoid.modernbetatab.mixin;

import me.kimovoid.modernbetatab.ModernBetaTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
    private void checkServer(Minecraft mc, String address, int port, CallbackInfo ci) {
        ModernBetaTab.playingModernBeta = address.contains("modernbeta.org");
        if (ModernBetaTab.playingModernBeta) {
            ModernBetaTab.LOGGER.info("Connected to Modern Beta SMP!");
        }
    }
}