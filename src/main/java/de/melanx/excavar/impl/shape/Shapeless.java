package de.melanx.excavar.impl.shape;

import com.google.common.collect.Lists;
import de.melanx.excavar.api.shape.Matcher;
import de.melanx.excavar.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class Shapeless implements Shape {

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
    public int addNeighbors(Level level, BlockPos pos, Direction side, BlockState originalState, List<BlockPos> blocksToMine, int stepsLeft) {
        //noinspection DuplicatedCode
        BlockPos.MutableBlockPos newPos = pos.mutable();
        for (Direction value : Direction.values()) {
            //noinspection DuplicatedCode
            if (stepsLeft > 0) {
                newPos.move(value);
                if (!blocksToMine.contains(newPos) && Matcher.SAME_BLOCK.test(originalState, level.getBlockState(newPos))) {
                    blocksToMine.add(newPos.immutable());
                    stepsLeft--;
                }
                newPos.set(pos);
            } else {
                return stepsLeft;
            }
        }
        newPos.set(pos);
        for (BlockPos offset : CORNER_OFFSETS) {
            //noinspection DuplicatedCode
            if (stepsLeft > 0) {
                newPos.move(offset);
                if (!blocksToMine.contains(newPos) && Matcher.SAME_BLOCK.test(originalState, level.getBlockState(newPos))) {
                    blocksToMine.add(newPos.immutable());
                    stepsLeft--;
                }
                newPos.set(pos);
            } else {
                return stepsLeft;
            }
        }

        return stepsLeft;
    }
}
