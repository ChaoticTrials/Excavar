package de.melanx.excavar.api;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.melanx.excavar.api.shape.Shapes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerHandler {

    private final Map<UUID, ClientData> players = Maps.newHashMap();
    private final Set<UUID> diggers = Sets.newHashSet();

    /**
     * @param id The {@link UUID} of the player which starts digging
     */
    public void startDigging(UUID id) {
        this.diggers.add(id);
    }

    /**
     * @param id The {@link UUID} of the player which stops digging
     */
    public void stopDigging(UUID id) {
        this.diggers.remove(id);
    }

    /**
     * Checks whether the {@code player} can mine multiple blocks.
     * Considers the {@link ClientData}.
     * Considers whether the {@code player} is digging.
     */
    public boolean canDig(Player player) {
        UUID id = player.getGameProfile().getId();
        return this.players.containsKey(id) && (!this.players.get(id).requiresSneaking || player.isShiftKeyDown()) && !this.diggers.contains(id);
    }

    /**
     * Provides the selected shape id of the given player.
     */
    public ResourceLocation getShapeId(UUID id) {
        ClientData data = this.players.get(id);
        return data == null ? Shapes.getSelectedShape() : data.shapeId();
    }

    @Deprecated(forRemoval = true)
    public void addPlayer(UUID id, ClientData data) {
        this.putPlayer(id, data);
    }

    /**
     * Adds a player to the players which could use it.
     *
     * @param id   The {@link UUID} of the player
     * @param data The {@link ClientData} of the player
     */
    public void putPlayer(UUID id, ClientData data) {
        this.players.put(id, data);
    }

    /**
     * Removes a player from the players which could use it.
     *
     * @param id The {@link UUID} of the player
     */
    public void removePlayer(UUID id) {
        this.players.remove(id);
    }

    /**
     * @param id The {@link UUID} of the player to get the data from
     * @return The {@link ClientData} of the player.
     */
    @Nonnull
    public ClientData getData(UUID id) {
        return this.players.getOrDefault(id, ClientData.EMPTY);
    }

    /**
     * Used to store the client config in the {@link PlayerHandler}
     */
    public record ClientData(boolean requiresSneaking, boolean preventToolBreaking, ResourceLocation shapeId) {

        private static final ResourceLocation MISSINGNO = new ResourceLocation("minecraft", "missingno");
        public static final ClientData EMPTY = new ClientData(false, false, MISSINGNO);
    }
}
