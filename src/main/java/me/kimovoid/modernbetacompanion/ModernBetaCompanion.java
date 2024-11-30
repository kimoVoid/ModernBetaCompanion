package me.kimovoid.modernbetacompanion;

import net.minecraft.client.Minecraft;

public class ModernBetaCompanion {

	public static boolean isPlayingModernBeta() {
		return Minecraft.INSTANCE.getNetworkHandler() != null
				&& Minecraft.INSTANCE.getNetworkHandler().connection != null
				&& Minecraft.INSTANCE.getNetworkHandler().connection.socket.getInetAddress().toString().contains("modernbeta.org");
	}
}