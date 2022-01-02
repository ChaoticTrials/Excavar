package de.melanx.excavar.api.events;

import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class DiggingEvent extends Event {

    private final ServerWorld world;
    private final ServerPlayerEntity player;
    private final List<BlockPos> positions;
    private final Block block;

    private DiggingEvent(ServerWorld world, ServerPlayerEntity player, List<BlockPos> positions, Block block) {
        this.world = world;
        this.player = player;
        this.positions = positions;
        this.block = block;
    }

    /**
     * @return The level were the blocks will be broken
     */
    public ServerWorld getLevel() {
        return this.world;
    }

    /**
     * @return The digger
     */
    public ServerPlayerEntity getPlayer() {
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

        public Pre(ServerWorld world, ServerPlayerEntity player, List<BlockPos> positions, Block block) {
            super(world, player, positions, block);
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


        public FoundPositions(ServerWorld world, ServerPlayerEntity player, List<BlockPos> positions, Block block) {
            super(world, player, positions, block);
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

        public Post(ServerWorld world, ServerPlayerEntity player, List<BlockPos> positions, Block block) {
            super(world, player, positions, block);
        }
    }
}
