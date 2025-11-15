package de.melanx.excavar.impl.shape;

import de.melanx.excavar.api.shape.Matcher;
import de.melanx.excavar.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BigTunnel implements Shape {

    @Override
    public void addNeighbors(Level level, BlockPos.MutableBlockPos pos, Direction forwardDirection, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks) {
        if (maxBlocks <= 0) {
            return;
        }

        Direction[] planeAxes = this.crossSectionAxes(forwardDirection);
        Direction axisA = planeAxes[0];
        Direction axisB = planeAxes[1];

        if (Matcher.SAME_BLOCK.test(originalState, level.getBlockState(pos))) {
            blocksToMine.add(pos.immutable());
            maxBlocks--;
        }

        while (maxBlocks > 0) {
            List<BlockPos> layer = new ArrayList<>();

            // 3x3 ring around a center in the plane spanned by axisA/axisB
            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    if (a == 0 && b == 0) {
                        // skip center, we handle forward separately
                        continue;
                    }

                    BlockPos candidate = pos
                            .relative(axisA, a)
                            .relative(axisB, b);

                    if (Matcher.SAME_BLOCK.test(originalState, level.getBlockState(candidate))) {
                        layer.add(candidate);
                    }
                }
            }

            // forward center block
            pos = pos.move(forwardDirection);
            if (Matcher.SAME_BLOCK.test(originalState, level.getBlockState(pos))) {
                layer.add(pos.immutable());
            }

            if (layer.isEmpty()) {
                break; // nothing more to dig in this direction
            }

            // don’t exceed maxBlocks
            int canAdd = Math.min(maxBlocks, layer.size());
            blocksToMine.addAll(layer.subList(0, canAdd));
            maxBlocks -= canAdd;
        }
    }

    private Direction[] crossSectionAxes(Direction forward) {
        return switch(forward) {
            case UP, DOWN -> new Direction[]{Direction.EAST, Direction.SOUTH};  // plane XZ
            case NORTH, SOUTH -> new Direction[]{Direction.EAST, Direction.UP}; // plane XY
            case EAST, WEST -> new Direction[]{Direction.SOUTH, Direction.UP};  // plane YZ
        };
    }
}
