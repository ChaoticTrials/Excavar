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
    private final boolean requiresCorrectTool;

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(BlockPos, Level, ServerPlayer, Predicate, boolean, boolean)
     */
    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull ServerPlayer player) {
        this(start, level, player, block -> block == level.getBlockState(start).getBlock());
    }

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(BlockPos, Level, ServerPlayer, Predicate, boolean, boolean)
     */
    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull ServerPlayer player, @Nonnull Predicate<Block> filter) {
        this(start, level, player, filter, ConfigHandler.requiresCorrectTool.get());
    }

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(BlockPos, Level, ServerPlayer, Predicate, boolean, boolean)
     */
    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull ServerPlayer player, @Nonnull Predicate<Block> filter, boolean requiresCorrectTool) {
        this(start, level, player, filter, requiresCorrectTool, Excavar.getPlayerHandler().getData(player.getGameProfile().getId()).preventToolBreaking());
    }

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(BlockPos, Level, ServerPlayer, Predicate, boolean, boolean)
     */
    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull ServerPlayer player, boolean requiresCorrectTool) {
        this(start, level, player, block -> block == level.getBlockState(start).getBlock(), requiresCorrectTool, Excavar.getPlayerHandler().getData(player.getGameProfile().getId()).preventToolBreaking());
    }

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(BlockPos, Level, ServerPlayer, Predicate, boolean, boolean)
     */
    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull ServerPlayer player, boolean requiresCorrectTool, boolean preventToolBreaking) {
        this(start, level, player, block -> block == level.getBlockState(start).getBlock(), requiresCorrectTool, preventToolBreaking);
    }

    /**
     * Creates a new instance of Excavador
     *
     * @param start               The base block position where to start searching for other {@link BlockPos}es.
     * @param level               The {@link Level} where all the magic happens.
     * @param player              The {@link ServerPlayer} which breaks the blocks.
     * @param filter              The filter to check whether a {@link BlockPos} will be added to the list which positions shall be destroyed.
     * @param requiresCorrectTool Whether the tool is a correct tool to generate drops.
     */
    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull ServerPlayer player, @Nonnull Predicate<Block> filter, boolean requiresCorrectTool, boolean preventToolBreaking) {
        this.start = start;
        this.level = level;
        this.player = player;
        this.testBlock = filter;
        this.preventToolBreaking = preventToolBreaking;
        this.requiresCorrectTool = requiresCorrectTool;
    }

    /**
     * Searches for the {@link BlockPos}es to break
     */
    public void findBlocks() {
        int limit = ConfigHandler.blockLimit.get();
        this.blocksToMine.add(this.start);
        Set<BlockPos> usedBlocks = Sets.newHashSet();
        limit--;
        BlockPos start = this.start;

        // find only start block when it's not the correct tool, but it's required via config
        if (this.requiresCorrectTool && !ForgeHooks.isCorrectToolForDrops(this.level.getBlockState(start), this.player)) {
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

    /**
     * Searches for neighbor blocks
     *
     * @param pos       Base {@link BlockPos} from where to check the neighbors
     * @param stepsLeft The amount of blocks to add to the list
     * @return Remaining {@code stepsLeft} after finding all neighbors.
     */
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

    /**
     * Mines all the blocks
     *
     * @param tool The tool which will be used to mine all the blocks
     */
    public void mine(ItemStack tool) {
        int stopAt = this.preventToolBreaking ? 2 : 1;
        for (BlockPos pos : this.blocksToMine) {
            this.player.gameMode.destroyBlock(pos);
            if (tool.isDamageableItem() && tool.getMaxDamage() - tool.getDamageValue() <= stopAt && !this.player.isCreative()) {
                break;
            }
        }
    }

    /**
     * @return The list which contains all positions where the player will be mining
     */
    public List<BlockPos> getBlocksToMine() {
        return this.blocksToMine;
    }
}
