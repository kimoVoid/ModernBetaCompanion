package me.kimovoid.modernbetacompanion.mixin.tab;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.modernbetacompanion.ModernBetaCompanion;
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
	@Unique private String currentPlayers = "";
	@Unique private int readingTicks = 0;
	@Unique private boolean doneReadingInitialMessage = false;

	@Inject(method = "tick", at = @At("HEAD"))
	private void switchReadingMessages(CallbackInfo ci) {
		if (this.readingTicks > 0 && !this.doneReadingInitialMessage) {
			this.readingTicks++;
			if (this.readingTicks < 3) return;
			this.doneReadingInitialMessage = true;
			//ModernBetaTab.LOGGER.info("Finished reading player list: {}", this.currentPlayers);
			for (String player : this.currentPlayers.split(", ")) {
				BetaQOL.INSTANCE.tabPlayers.put(player, 1);
			}
		}
	}

	@Inject(method = "handleChatMessage", at = @At("TAIL"))
	private void parseMessage(ChatMessagePacket packet, CallbackInfo ci) {
		if (!ModernBetaCompanion.isPlayingModernBeta()) return;

		String message = STRIP_COLOR_PATTERN.matcher(packet.message).replaceAll("");
		if (message.startsWith("[")) return; // In some rare cases, the mod would read chat messages. This avoids it.

		/* Initial join message with all current players */
		if (message.contains("Currently online: ")) {
			this.currentPlayers = message.split("Currently online: ")[1];
			this.readingTicks++;
			return;
		}

		/* Read more initial "current players" messages */
		if (this.readingTicks <= 3 && !this.doneReadingInitialMessage) {
			this.currentPlayers += message;
			return;
		}

		if (!this.doneReadingInitialMessage || !message.startsWith("§e")) return;

		/* Join message */
		if (message.contains(" joined the game")) {
			String name = message.contains(" (formerly") ? message.split(" (formerly")[0] : message.split(" joined")[0];
			BetaQOL.INSTANCE.tabPlayers.put(name, 1);
		}

		/* Quit message */
		if (message.endsWith(" left the game")) {
			String name = message.split(" left")[0];
			BetaQOL.INSTANCE.tabPlayers.remove(name);
		}
	}
}