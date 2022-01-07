package de.melanx.excavar.impl.shape;

import de.melanx.excavar.api.shape.Matcher;
import de.melanx.excavar.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class EasyShapeless implements Shape {

    @Override
    public int addNeighbors(Level level, BlockPos pos, Direction side, BlockState originalState, List<BlockPos> blocksToMine, int stepsLeft) {
        //noinspection DuplicatedCode
        BlockPos.MutableBlockPos newPos = pos.mutable();
        for (Direction value : Direction.values()) {
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

        return stepsLeft;
    }
}
