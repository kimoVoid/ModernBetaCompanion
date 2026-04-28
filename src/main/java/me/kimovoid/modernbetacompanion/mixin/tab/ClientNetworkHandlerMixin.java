package me.kimovoid.modernbetacompanion.mixin.tab;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.modernbetacompanion.ModernBetaCompanion;
import me.kimovoid.modernbetacompanion.util.TabProvider;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.packet.ChatMessagePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {

    @Unique private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-OR]");

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void onServerJoin(CallbackInfo ci) {
        if (!ModernBetaCompanion.isPlayingModernBeta()) return;

        try {
            String[] players = TabProvider.INSTANCE.getPlayers().get();
            for (String player : players) {
                BetaQOL.INSTANCE.tabPlayers.put(player, 1);
            }
        } catch (Exception ignored) {
        }
    }

    @Inject(method = "handleChatMessage", at = @At("TAIL"))
    private void parseMessage(ChatMessagePacket packet, CallbackInfo ci) {
        if (!ModernBetaCompanion.isPlayingModernBeta()) return;

        String message = STRIP_COLOR_PATTERN.matcher(packet.message).replaceAll("");
        if (message.startsWith("[") || message.startsWith("§")) return;

        /* Join message */
        if (message.contains(" joined the game")) {
            String name = message.contains(" (formerly") ? message.split(" \\(formerly")[0] : message.split(" joined")[0];
            BetaQOL.INSTANCE.tabPlayers.put(name, 1);
        }

        /* Quit message */
        if (message.endsWith(" left the game")) {
            String name = message.split(" left")[0];
            BetaQOL.INSTANCE.tabPlayers.remove(name);
        }
    }
}
