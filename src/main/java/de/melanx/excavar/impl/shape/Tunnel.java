package de.melanx.excavar.impl.shape;

import de.melanx.excavar.api.shape.Matcher;
import de.melanx.excavar.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class Tunnel implements Shape {

    @Override
    public int addNeighbors(Level level, BlockPos pos, Direction side, BlockState originalState, List<BlockPos> blocksToMine, int stepsLeft) {
        BlockPos neighbor = pos.relative(side.getOpposite());
        if (stepsLeft > 0 && !blocksToMine.contains(neighbor) && Matcher.SAME_BLOCK.test(originalState, level.getBlockState(neighbor))) {
            blocksToMine.add(neighbor);
            stepsLeft--;
        }

        return stepsLeft;
    }
}
