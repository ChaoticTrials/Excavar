package de.melanx.excavar;

import de.melanx.excavar.api.shape.Shapes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public class ShapeUtil {

    public static boolean miningAllowed(BlockState state) {
        Type configured = ConfigHandler.allowedBlocks.get();
        return switch (configured) {
            case ALL -> true;
            case ORES_AND_LOGS -> Tags.Blocks.ORES.contains(state.getBlock()) || BlockTags.LOGS.contains(state.getBlock());
            case ORES -> Tags.Blocks.ORES.contains(state.getBlock());
            case LOGS -> BlockTags.LOGS.contains(state.getBlock());
        };
    }

    public static ResourceLocation getShapeId(Block block) {
        if (block instanceof BushBlock) {
            return Shapes.PLANTS_SHAPELESS;
        }

        return Shapes.SHAPELESS;
    }

    enum Type {
        ORES,
        LOGS,
        ORES_AND_LOGS,
        ALL
    }
}
