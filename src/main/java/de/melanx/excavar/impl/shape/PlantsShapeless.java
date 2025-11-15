package de.melanx.excavar.impl.shape;

import com.google.common.collect.Lists;
import de.melanx.excavar.api.shape.Matcher;
import de.melanx.excavar.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PlantsShapeless extends Shapeless implements Shape {

    private static final List<BlockPos> CORNER_OFFSETS = Lists.newArrayList(
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 0),
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, 0),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(0, 0, 2),
            new BlockPos(0, 0, -2),
            new BlockPos(1, 0, 2),
            new BlockPos(1, 0, -2),
            new BlockPos(-1, 0, 2),
            new BlockPos(-1, 0, -2),
            new BlockPos(2, 0, 0),
            new BlockPos(2, 0, 1),
            new BlockPos(2, 0, -1),
            new BlockPos(2, 0, 2),
            new BlockPos(2, 0, -2),
            new BlockPos(-2, 0, 0),
            new BlockPos(-2, 0, 1),
            new BlockPos(-2, 0, -1),
            new BlockPos(-2, 0, 2),
            new BlockPos(-2, 0, -2)
    );

    @Override
    public void addNeighbors(Level level, BlockPos.MutableBlockPos pos, Direction forwardDirection, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks) {
        this.addNeighbors(level, pos, originalState, blocksToMine, maxBlocks, pos.immutable());
    }

    @Override
    protected Iterable<Direction> directions() {
        return Direction.Plane.HORIZONTAL;
    }

    @Override
    protected List<BlockPos> cornerOffsets() {
        return CORNER_OFFSETS;
    }

    @Override
    protected Matcher blockMatcher() {
        return Matcher.PLANT;
    }
}
