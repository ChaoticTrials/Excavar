package de.melanx.excavar.impl.shape;

import com.google.common.collect.Lists;
import de.melanx.excavar.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Shapeless implements Shape {

    private static final Iterable<Direction> DIRECTIONS = List.of(Direction.values());
    private static final List<BlockPos> CORNER_OFFSETS = Lists.newArrayList(
            new BlockPos(0, -1, -1),
            new BlockPos(0, -1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(0, 1, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, -1, 0),
            new BlockPos(-1, -1, -1),
            new BlockPos(-1, -1, 1),
            new BlockPos(-1, 1, 0),
            new BlockPos(-1, 1, -1),
            new BlockPos(-1, 1, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(1, 0, 1),
            new BlockPos(1, -1, 0),
            new BlockPos(1, -1, -1),
            new BlockPos(1, -1, 1),
            new BlockPos(1, 1, 0),
            new BlockPos(1, 1, -1),
            new BlockPos(1, 1, 1)
    );

    @Override
    public void addNeighbors(Level level, BlockPos.MutableBlockPos pos, Direction forwardDirection, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks) {
        BlockPos mainPos = pos.immutable();
        this.addNeighbors(level, pos, originalState, blocksToMine, maxBlocks, mainPos);
    }

    protected Iterable<Direction> directions() {
        return DIRECTIONS;
    }

    protected List<BlockPos> cornerOffsets() {
        return CORNER_OFFSETS;
    }

    protected void addNeighbors(Level level, BlockPos.MutableBlockPos pos, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks, BlockPos mainPos) {
        if (maxBlocks <= 0) {
            return;
        }

        Set<BlockPos> usedBlocks = new HashSet<>();

        while (maxBlocks > 0) {
            if (mainPos == null) {
                break;
            }

            pos.set(mainPos);
            usedBlocks.add(mainPos);
            maxBlocks = this.tryAddByDirections(level, pos, mainPos, originalState, blocksToMine, maxBlocks);
            maxBlocks = this.tryAddByOffsets(level, pos, mainPos, originalState, blocksToMine, maxBlocks);

            mainPos = blocksToMine.stream()
                    .filter(p -> !usedBlocks.contains(p))
                    .findFirst()
                    .orElse(null);
        }
    }

    protected int tryAddBlock(Level level, BlockPos.MutableBlockPos pos, BlockPos originalPos, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks) {
        if (this.canAddBlock(level, pos, originalState, blocksToMine, maxBlocks)) {
            blocksToMine.add(pos.immutable());
            maxBlocks--;
        }

        pos.set(originalPos);
        return maxBlocks;
    }

    protected int tryAddByDirections(Level level, BlockPos.MutableBlockPos pos, BlockPos originalPos, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks) {
        for (Direction direction : this.directions()) {
            pos.move(direction);
            maxBlocks = this.tryAddBlock(level, pos, originalPos, originalState, blocksToMine, maxBlocks);
        }

        return maxBlocks;
    }

    protected int tryAddByOffsets(Level level, BlockPos.MutableBlockPos pos, BlockPos originalPos, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks) {
        for (BlockPos offset : this.cornerOffsets()) {
            pos.move(offset);
            maxBlocks = this.tryAddBlock(level, pos, originalPos, originalState, blocksToMine, maxBlocks);
        }

        return maxBlocks;
    }
}
