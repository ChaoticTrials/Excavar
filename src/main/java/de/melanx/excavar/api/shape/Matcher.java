package de.melanx.excavar.api.shape;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An interface to test if the original and current {@link BlockState} match.
 */
public interface Matcher {

    boolean test(BlockState original, BlockState state);

    Matcher SAME_BLOCK = (original, state) -> original.getBlock() == state.getBlock();
    Matcher PLANT = (original, state) -> state.getBlock() instanceof BushBlock bush && Matcher.plantType(bush) == Matcher.plantType(original.getBlock());

    private static PlantType plantType(Block block) {
        if (block instanceof CropBlock) {
            return PlantType.CROP;
        } else if (block instanceof SaplingBlock) {
            return PlantType.SAPLING;
        } else if (block instanceof FlowerBlock) {
            return PlantType.FLOWER;
        }

        return PlantType.GRASS;
    }

    enum PlantType {
        CROP,
        SAPLING,
        FLOWER,
        GRASS
    }
}
