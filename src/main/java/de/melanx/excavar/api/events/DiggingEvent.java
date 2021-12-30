package de.melanx.excavar.api.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class DiggingEvent extends Event {

    private final ServerLevel level;
    private final ServerPlayer player;
    private final List<BlockPos> positions;
    private final Block block;

    private DiggingEvent(ServerLevel level, ServerPlayer player, List<BlockPos> positions, Block block) {
        this.level = level;
        this.player = player;
        this.positions = positions;
        this.block = block;
    }

    /**
     * @return The level were the blocks will be broken
     */
    public ServerLevel getLevel() {
        return this.level;
    }

    /**
     * @return The digger
     */
    public ServerPlayer getPlayer() {
        return this.player;
    }

    /**
     * @return A list of all positions which will be broken
     */
    public List<BlockPos> getPositions() {
        return this.positions;
    }

    /**
     * @return The block type which will be broken
     */
    public Block getBlock() {
        return this.block;
    }

    /**
     * Fired before even the blocks will be discovered. {@link Pre#getPositions()} is always empty.
     * <p>
     * If canceled, no blocks will be searched, nothing will be broken.
     */
    public static class Pre extends DiggingEvent {

        public Pre(ServerLevel level, ServerPlayer player, List<BlockPos> positions, Block block) {
            super(level, player, positions, block);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }
    }

    /**
     * Fired after blocks were discovered and before the blocks will be mined.
     * <p>
     * If canceled, nothing will be broken.
     */
    public static class FoundPositions extends DiggingEvent {


        public FoundPositions(ServerLevel level, ServerPlayer player, List<BlockPos> positions, Block block) {
            super(level, player, positions, block);
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

    }

    /**
     * Fired after blocks were mined.
     */
    public static class Post extends DiggingEvent {

        public Post(ServerLevel level, ServerPlayer player, List<BlockPos> positions, Block block) {
            super(level, player, positions, block);
        }
    }
}
