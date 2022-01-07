package de.melanx.excavar.impl.shape;

import com.google.common.collect.Lists;
import de.melanx.excavar.api.shape.Matcher;
import de.melanx.excavar.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PlantsShapeless implements Shape {

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
    public int addNeighbors(Level level, BlockPos pos, Direction side, BlockState originalState, List<BlockPos> blocksToMine, int stepsLeft) {
        BlockPos.MutableBlockPos newPos = pos.mutable();
        for (Direction value : Direction.Plane.HORIZONTAL) {
            if (stepsLeft > 0) {
                newPos.move(value);
                if (!blocksToMine.contains(newPos) && Matcher.PLANT.test(originalState, level.getBlockState(newPos))) {
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
            if (stepsLeft > 0) {
                newPos.move(offset);
                if (!blocksToMine.contains(newPos) && Matcher.PLANT.test(originalState, level.getBlockState(newPos))) {
                    blocksToMine.add(newPos.immutable());
                    stepsLeft--;
                }
                newPos.set(pos);
            }
        }

        return stepsLeft;
    }
}
