package de.melanx.excavar.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Excavador {

    private final BlockPos start;
    private final World world;
    private final ServerPlayerEntity player;
    private final Predicate<Block> testBlock;
    private final List<BlockPos> blocksToMine = Lists.newArrayList();
    private final boolean preventToolBreaking;

    public Excavador(@Nonnull BlockPos start, @Nonnull World world, @Nonnull ServerPlayerEntity player) {
        this.start = start;
        this.world = world;
        this.player = player;
        this.testBlock = block -> block == this.world.getBlockState(start).getBlock();
        this.preventToolBreaking = Excavar.getPlayerHandler().getData(player.getGameProfile().getId()).preventToolBreaking();
    }

    public void findBlocks() {
        int limit = ConfigHandler.blockLimit.get();
        this.blocksToMine.add(this.start);
        Set<BlockPos> usedBlocks = Sets.newHashSet();
        limit--;
        BlockPos start = this.start;

        // find only start block when it's not the correct tool, but it's required via config
        if (ConfigHandler.requiresCorrectTool.get() && !ForgeHooks.canHarvestBlock(this.world.getBlockState(start), this.player, this.world, start)) {
            return;
        }

        while (limit > 0) {
            if (start == null) {
                break;
            }
            usedBlocks.add(start);
            limit = this.addNeighbors(start, limit);
            start = this.blocksToMine.stream()
                    .filter(pos -> !usedBlocks.contains(pos))
                    .findFirst()
                    .orElse(null);
        }
    }

    public int addNeighbors(BlockPos pos, int stepsLeft) {
        int[] neighborPos = new int[]{0, -1, 1};
        for (int y : neighborPos) {
            for (int x : neighborPos) {
                for (int z : neighborPos) {
                    if (stepsLeft > 0) {
                        BlockPos newPos = pos.add(x, y, z);
                        if (this.testBlock.test(this.world.getBlockState(newPos).getBlock())) {
                            if (!this.blocksToMine.contains(newPos)) {
                                this.blocksToMine.add(newPos);
                                stepsLeft--;
                            }
                        }
                    } else {
                        return stepsLeft;
                    }
                }
            }
        }

        return stepsLeft;
    }

    public void mine(ItemStack tool) {
        int stopAt = this.preventToolBreaking ? 2 : 1;
        for (BlockPos pos : this.blocksToMine) {
            this.player.interactionManager.tryHarvestBlock(pos);
            if (tool.isDamageable() && tool.getMaxDamage() - tool.getDamage() <= stopAt && !this.player.isCreative()) {
                break;
            }
        }
    }

    public List<BlockPos> getBlocksToMine() {
        return this.blocksToMine;
    }
}
