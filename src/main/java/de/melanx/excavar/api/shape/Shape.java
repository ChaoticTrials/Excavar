package de.melanx.excavar.api.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.List;

public interface Shape {

    /**
     * Searches for neighbor blocks
     *
     * @param level            The {@link Level} where the blocks will be mined
     * @param pos              Base {@link BlockPos} from where to check the neighbors
     * @param forwardDirection The {@link Direction} the player is facing
     * @param originalState    The filter for filtering the block
     * @param blocksToMine     A {@link List} which contains already added {@link BlockPos}
     * @param maxBlocks        The number of blocks to add to the list
     */
    void addNeighbors(Level level, Player player, BlockPos.MutableBlockPos pos, Direction forwardDirection, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks);

    @Nonnull
    default Matcher blockMatcher() {
        return Matcher.SAME_BLOCK;
    }

    default boolean canAddBlock(Level level, Player player, BlockPos pos, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks) {
        BlockState otherState = level.getBlockState(pos);

        return maxBlocks > 0
                && !blocksToMine.contains(pos)
                && this.blockMatcher().test(originalState, otherState)
                && otherState.canHarvestBlock(level, pos, player);
    }
}
