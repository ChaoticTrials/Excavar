package de.melanx.excavar.api;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.UUID;

public class PlayerHandler {

    private final Set<UUID> players = Sets.newHashSet();
    private final Set<UUID> diggers = Sets.newHashSet();

    public void startDigging(UUID id) {
        this.diggers.add(id);
    }

    public void stopDigging(UUID id) {
        this.diggers.remove(id);
    }

    public boolean canDig(UUID id) {
        return this.players.contains(id) && !this.diggers.contains(id);
    }

    public void addPlayer(UUID id) {
        this.players.add(id);
    }

    public void removePlayer(UUID id) {
        this.players.remove(id);
    }
}
