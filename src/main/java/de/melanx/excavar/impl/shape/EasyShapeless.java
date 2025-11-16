package de.melanx.excavar.impl.shape;

import de.melanx.excavar.api.shape.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class EasyShapeless extends Shapeless implements Shape {

    @Override
    public void addNeighbors(Level level, Player player, BlockPos.MutableBlockPos pos, Direction forwardDirection, BlockState originalState, List<BlockPos> blocksToMine, int maxBlocks) {
        this.addNeighbors(level, player, pos, originalState, blocksToMine, maxBlocks, pos.immutable());
    }

    @Override
    protected List<BlockPos> cornerOffsets() {
        return List.of();
    }
}
