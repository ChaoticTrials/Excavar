package de.melanx.excavar.api;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerHandler {

    private final Map<UUID, ClientData> players = Maps.newHashMap();
    private final Set<UUID> diggers = Sets.newHashSet();

    public void startDigging(UUID id) {
        this.diggers.add(id);
    }

    public void stopDigging(UUID id) {
        this.diggers.remove(id);
    }

    public boolean canDig(Player player) {
        UUID id = player.getGameProfile().getId();
        return this.players.containsKey(id) && (!this.players.get(id).requiresSneaking || player.isShiftKeyDown()) && !this.diggers.contains(id);
    }

    public void addPlayer(UUID id, ClientData data) {
        this.players.put(id, data);
    }

    public void removePlayer(UUID id) {
        this.players.remove(id);
    }

    @Nonnull
    public ClientData getData(UUID id) {
        return this.players.getOrDefault(id, null);
    }

    public record ClientData(boolean requiresSneaking, boolean preventToolBreaking) {

        public static final ClientData EMPTY = new ClientData(false, false);
    }
}
