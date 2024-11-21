package me.kimovoid.modernbetatab.mixin;

import me.kimovoid.betaqol.BetaQOL;
import me.kimovoid.modernbetatab.ModernBetaTab;
import net.minecraft.client.network.handler.ClientNetworkHandler;
import net.minecraft.network.packet.ChatMessagePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientNetworkHandler.class)
public class ClientNetworkHandlerMixin {

	@Unique private String currentPlayers = "";
	@Unique private boolean readingInitialMessage = false;

	@Inject(method = "tick", at = @At("HEAD"))
	private void switchReadingMessages(CallbackInfo ci) {
		if (this.readingInitialMessage) {
			this.readingInitialMessage = false;
			//ModernBetaTab.LOGGER.info("Finished reading player list: {}", this.currentPlayers);
			for (String player : this.currentPlayers.split(", ")) {
				BetaQOL.INSTANCE.tabPlayers.put(player, 10);
			}
		}
	}

	@Inject(method = "handleChatMessage", at = @At("TAIL"))
	private void parseMessage(ChatMessagePacket packet, CallbackInfo ci) {
		if (!ModernBetaTab.playingModernBeta) return;

		/* Initial join message with all current players */
		if (packet.message.contains("Currently online: ")) {
			this.currentPlayers = packet.message.split("Currently online: ")[1];
			this.readingInitialMessage = true;
			return;
		}

		/* Read more initial "current players" messages */
		if (this.readingInitialMessage) {
			this.currentPlayers += packet.message;
			return;
		}

		/* Join message */
		if (packet.message.contains("§e joined the game")) {
			String name = packet.message
					.split("§e joined")[0]
					.substring(4);
			BetaQOL.INSTANCE.tabPlayers.put(name, 1);
		}

		/* Quit message */
		if (packet.message.endsWith("§e left the game")) {
			String name = packet.message
					.split("§e left")[0]
					.substring(4);
			BetaQOL.INSTANCE.tabPlayers.remove(name);
		}
	}
}