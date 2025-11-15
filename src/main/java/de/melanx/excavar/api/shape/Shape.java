package de.melanx.excavar.api.shape;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface Shape {

    @Deprecated(forRemoval = true)
    default int addNeighbors(Level level, BlockPos pos, Direction side, BlockState originalState, List<BlockPos> blocksToMine, int stepsLeft) {
        throw new UnsupportedOperationException("This method is deprecated and should not be used anymore.");
    }

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
    void addNeighbors(Level level, BlockPos.MutableBlockPos pos, Direction forwardDirection, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks);
}
