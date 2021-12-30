package de.melanx.excavar.api;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerHandler {

    private final Map<UUID, Boolean> players = Maps.newHashMap();
    private final Set<UUID> diggers = Sets.newHashSet();

    public void startDigging(UUID id) {
        this.diggers.add(id);
    }

    public void stopDigging(UUID id) {
        this.diggers.remove(id);
    }

    public boolean canDig(Player player) {
        UUID id = player.getGameProfile().getId();
        return this.players.containsKey(id) && (!this.players.get(id) || player.isShiftKeyDown()) && !this.diggers.contains(id);
    }

    public void addPlayer(UUID id, boolean requiresSneaking) {
        this.players.put(id, requiresSneaking);
    }

    public void removePlayer(UUID id) {
        this.players.remove(id);
    }
}
