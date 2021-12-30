package de.melanx.excavar;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeHooks;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class Excavador {

    private final BlockPos start;
    private final Level level;
    private final Player player;
    private final Predicate<Block> testBlock;
    private final Set<BlockPos> blocksToMine = Sets.newHashSet();
    private final List<BlockPos> knownBlocks = Lists.newArrayList();

    public Excavador(BlockPos start, Level level, Player player) {
        this.start = start;
        this.level = level;
        this.player = player;
        this.testBlock = block -> block == this.level.getBlockState(start).getBlock();
    }

    public void findBlocks(ItemStack tool) {
        int limit;
        if (tool.isDamageableItem()) {
            limit = Math.min(tool.getMaxDamage() - tool.getDamageValue(), ConfigHandler.blockLimit.get());
        } else {
            limit = ConfigHandler.blockLimit.get();
        }
        this.blocksToMine.add(this.start);
        this.knownBlocks.add(this.start);
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
            start = this.knownBlocks.stream()
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
                            if (!this.knownBlocks.contains(newPos)) {
                                this.blocksToMine.add(newPos);
                                this.knownBlocks.add(newPos);
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

    public void mine() {
        for (BlockPos pos : this.blocksToMine) {
            ((ServerPlayer) this.player).gameMode.destroyBlock(pos);
//            BlockState state = this.level.getBlockState(pos);
//            Block block = state.getBlock();
//            block.playerDestroy(this.level, this.player, pos, state, this.level.getBlockEntity(pos), this.player.getMainHandItem());
//            this.level.removeBlock(pos, false);
        }
    }
}
