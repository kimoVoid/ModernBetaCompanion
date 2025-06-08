package me.kimovoid.modernbetacompanion;

import me.kimovoid.betaqol.event.BetaQOLEvents;
import me.kimovoid.modernbetacompanion.cape.CapeService;
import net.minecraft.client.Minecraft;
import net.ornithemc.osl.entrypoints.api.client.ClientModInitializer;

public class ModernBetaCompanion implements ClientModInitializer {

	public static boolean isPlayingModernBeta() {
		return Minecraft.INSTANCE.getNetworkHandler() != null
				&& Minecraft.INSTANCE.getNetworkHandler().connection != null
				&& Minecraft.INSTANCE.getNetworkHandler().connection.socket != null
				&& Minecraft.INSTANCE.getNetworkHandler().connection.socket.getInetAddress() != null
				&& Minecraft.INSTANCE.getNetworkHandler().connection.socket.getInetAddress().toString().contains("modernbeta.org");
	}

	@Override
	public void initClient() {
		BetaQOLEvents.LOAD_SKIN.register(event -> {
			if (!isPlayingModernBeta()) return;
			CapeService.INSTANCE.init(event.getPlayer(), event);
		});

		BetaQOLEvents.RELOAD_SKIN.register(player -> {
			CapeService.INSTANCE.mbCapes.clear();
			if (player.cape != null && player.cape.contains("modernbeta")) {
				player.cape = player.cloak = null;
			}
		});
	}
}