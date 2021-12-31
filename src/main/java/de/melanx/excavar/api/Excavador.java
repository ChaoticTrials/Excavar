package de.melanx.excavar.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Excavador {

    private final BlockPos start;
    private final Level level;
    private final ServerPlayer player;
    private final Predicate<Block> testBlock;
    private final List<BlockPos> blocksToMine = Lists.newArrayList();
    private final boolean preventToolBreaking;

    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull ServerPlayer player) {
        this.start = start;
        this.level = level;
        this.player = player;
        this.testBlock = block -> block == this.level.getBlockState(start).getBlock();
        this.preventToolBreaking = Excavar.getPlayerHandler().getData(player.getGameProfile().getId()).preventToolBreaking();
    }

    public void findBlocks() {
        int limit = ConfigHandler.blockLimit.get();
        this.blocksToMine.add(this.start);
        Set<BlockPos> usedBlocks = Sets.newHashSet();
        limit--;
        BlockPos start = this.start;

        // find only start block when it's not the correct tool, but it's required via config
        if (ConfigHandler.requiresCorrectTool.get() && !ForgeHooks.isCorrectToolForDrops(this.level.getBlockState(start), this.player)) {
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
                        BlockPos newPos = pos.offset(x, y, z);
                        if (this.testBlock.test(this.level.getBlockState(newPos).getBlock())) {
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
            this.player.gameMode.destroyBlock(pos);
            if (tool.isDamageableItem() && tool.getMaxDamage() - tool.getDamageValue() <= stopAt && !this.player.isCreative()) {
                break;
            }
        }
    }

    public List<BlockPos> getBlocksToMine() {
        return this.blocksToMine;
    }
}
