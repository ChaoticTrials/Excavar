package de.melanx.excavar;

import de.melanx.excavar.api.shape.Shapes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

public class ShapeUtil {

    public static boolean miningAllowed(BlockState state) {
        Type configured = ConfigHandler.allowedBlocks.get();
        return switch (configured) {
            case ALL -> true;
            case ORES_AND_LOGS -> state.is(Tags.Blocks.ORES) || state.is(BlockTags.LOGS);
            case ORES -> state.is(Tags.Blocks.ORES);
            case LOGS -> state.is(BlockTags.LOGS);
        };
    }

    public static ResourceLocation getShapeId(Block block) {
        if (block instanceof BushBlock) {
            return Shapes.PLANTS_SHAPELESS;
        }

        return ConfigHandler.disableDiagonals.get() ? Shapes.EASY_SHAPELESS : Shapes.SHAPELESS;
    }

    public enum Type {
        ORES,
        LOGS,
        ORES_AND_LOGS,
        ALL
    }
}
