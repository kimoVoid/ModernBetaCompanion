package me.kimovoid.modernbetacompanion.util;

import me.kimovoid.betaqol.event.BetaQOLEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.living.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class CapeService {

    public static final CapeService INSTANCE = new CapeService();
    public final Map<String, String> mbCapes = new HashMap<>();

    private boolean updatePlayer(PlayerEntity player, BetaQOLEvents.LoadSkinEvent event) {
        if (!this.mbCapes.containsKey(player.name)) {
            return false;
        }

        if (event == null) {
            player.cape = player.cloak = this.mbCapes.get(player.name);
            Minecraft.INSTANCE.worldRenderer.onEntityAdded(player);
            return true;
        }

        event.setCapeUrl(this.mbCapes.get(player.name));
        return true;
    }

    public void init(PlayerEntity player, BetaQOLEvents.LoadSkinEvent event) {
        if (updatePlayer(player, event)) return;

        initCape(player.name);
        updatePlayer(player, event);
    }

    private void initCape(String name) {
        if (this.mbCapes.containsKey(name)) return;

        String capeUrl;

        try {
            capeUrl = new CapeProvider().getCape(name).get();
        } catch (Exception e) {
            return;
        }

        this.mbCapes.put(name, capeUrl);
    }
}