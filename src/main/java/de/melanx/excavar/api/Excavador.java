package de.melanx.excavar.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.shape.Shape;
import de.melanx.excavar.api.shape.Shapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class Excavador {

    public final BlockPos start;
    public final Level level;
    public final Player player;
    public final Direction side;
    private final BlockState originalState;
    private final List<BlockPos> blocksToMine = Lists.newArrayList();
    private final boolean preventToolBreaking;
    private final boolean requiresCorrectTool;
    private final Shape shape;

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(ResourceLocation, BlockPos, Level, Player, Direction, BlockState, boolean, boolean)
     */
    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull Player player, @Nonnull Direction side, @Nonnull BlockState originalState) {
        this(start, level, player, side, originalState, ConfigHandler.requiresCorrectTool.get());
    }

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(ResourceLocation, BlockPos, Level, Player, Direction, BlockState, boolean, boolean)
     */
    public Excavador(@Nonnull BlockPos start, @Nonnull Level level, @Nonnull Player player, @Nonnull Direction side, @Nonnull BlockState originalState, boolean requiresCorrectTool) {
        this(Shapes.getSelectedShape(), start, level, player, side, originalState, requiresCorrectTool, Excavar.getPlayerHandler().getData(player.getGameProfile().getId()).preventToolBreaking());
    }

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(ResourceLocation, BlockPos, Level, Player, Direction, BlockState, boolean, boolean)
     */
    public Excavador(@Nonnull ResourceLocation shapeId, @Nonnull BlockPos start, @Nonnull Level level, @Nonnull Player player, @Nonnull Direction side, @Nonnull BlockState originalState) {
        this(shapeId, start, level, player, side, originalState, ConfigHandler.requiresCorrectTool.get());
    }

    /**
     * Creates a new instance of Excavador
     *
     * @see #Excavador(ResourceLocation, BlockPos, Level, Player, Direction, BlockState, boolean, boolean)
     */
    public Excavador(@Nonnull ResourceLocation shapeId, @Nonnull BlockPos start, @Nonnull Level level, @Nonnull Player player, @Nonnull Direction side, @Nonnull BlockState originalState, boolean requiresCorrectTool) {
        this(shapeId, start, level, player, side, originalState, requiresCorrectTool, Excavar.getPlayerHandler().getData(player.getGameProfile().getId()).preventToolBreaking());
    }

    /**
     * Creates a new instance of Excavador
     *
     * @param shapeId             The {@link ResourceLocation} id of the registered {@link Shape} in {@link Shapes}
     * @param start               The base block position where to start searching for other {@link BlockPos}'
     * @param level               The {@link Level} where all the magic happens
     * @param player              The {@link Player} which breaks the blocks
     * @param side                The {@link Direction} side of the block the player is facing
     * @param originalState       The state to check whether a {@link BlockPos} will be added to the list which positions shall be destroyed
     * @param requiresCorrectTool Whether the tool is a correct tool to generate drops
     * @param preventToolBreaking Whether the tool should be saved while mining
     */
    public Excavador(@Nonnull ResourceLocation shapeId, @Nonnull BlockPos start, @Nonnull Level level, @Nonnull Player player, @Nonnull Direction side, @Nonnull BlockState originalState, boolean requiresCorrectTool, boolean preventToolBreaking) {
        this.start = start.immutable();
        this.level = level;
        this.player = player;
        this.side = side;
        this.originalState = originalState;
        this.preventToolBreaking = preventToolBreaking;
        this.requiresCorrectTool = requiresCorrectTool;
        this.shape = Shapes.getShape(shapeId);
    }

    public void findBlocks() {
        this.findBlocks(Integer.MAX_VALUE);
    }

    /**
     * Searches for the {@link BlockPos}es to break if not already searched.
     */
    public void findBlocks(int maxBlocks) {
        if (!this.blocksToMine.isEmpty()) return;
        int limit = Math.min(maxBlocks, ConfigHandler.blockLimit.get());
        this.blocksToMine.add(this.start);
        Set<BlockPos> usedBlocks = Sets.newHashSet();
        limit--;
        BlockPos start = this.start;

        // find only start block when it's not the correct tool, but it's required via config
        if (this.requiresCorrectTool && (!ForgeHooks.isCorrectToolForDrops(this.level.getBlockState(start), this.player)
                || (ConfigHandler.fistForbidden.get() && this.player.getMainHandItem().isEmpty() && this.level.getBlockState(start).getDestroySpeed(this.level, start) > 1))) {
            return;
        }

        while (limit > 0) {
            if (start == null) {
                break;
            }
            usedBlocks.add(start);
            limit = this.shape.addNeighbors(this.level, start, this.side, this.originalState, this.blocksToMine, limit);
            start = this.blocksToMine.stream()
                    .filter(pos -> !usedBlocks.contains(pos))
                    .findFirst()
                    .orElse(null);
        }
    }

    /**
     * Mines all the blocks
     *
     * @param tool The tool which will be used to mine all the blocks
     */
    public void mine(ItemStack tool) {
        int xp = Excavador.getExpPoints(this.player.experienceLevel, this.player.experienceProgress);
        int stopAt = this.preventToolBreaking ? 2 : 1; // we need to increase this by 1, otherwise the tool will be broken to early
        if (!(this.player instanceof ServerPlayer player)) {
            throw new IllegalStateException("Can't mine on client side");
        }

        int i = 0;
        for (BlockPos pos : this.blocksToMine) {
            boolean tooLessXp = xp - ConfigHandler.xpUsage.get() < 0;
            if ((tool.isDamageableItem() && tool.getMaxDamage() - tool.getDamageValue() <= stopAt || tooLessXp) && !player.isCreative()) {
                if (tooLessXp) {
                    player.sendSystemMessage(Component.translatable("excavar.config.xp_usage.missing", this.blocksToMine.size() - i - ConfigHandler.xpUsage.get()));
                }

                break;
            }

            player.gameMode.destroyBlock(pos);
            player.causeFoodExhaustion((float) (ConfigHandler.hungerUsage.get() - 0.005F));

            // prevent xp usage for first block
            if (i >= 1) {
                player.giveExperiencePoints(-ConfigHandler.xpUsage.get());
                xp -= ConfigHandler.xpUsage.get();
            }

            i++;
        }
    }

    /**
     * @return The list which contains all positions where the player will be mining
     */
    public List<BlockPos> getBlocksToMine() {
        return this.blocksToMine;
    }

    private static int getExpPoints(int level, float exp) {
        int points = 0;

        for (int i = 0; i < level; i++) {
            points += Excavador.getXpBarCap(i);
        }

        points += Math.round(Excavador.getXpBarCap(level) * exp);
        return points;
    }

    private static int getXpBarCap(int level) {
        if (level >= 30) {
            return 112 + ((level - 30) * 9);
        }

        if (level >= 15) {
            return 37 + ((level - 15) * 5);
        }

        if (level < 0) {
            return 0;
        }

        return 7 + (level * 2);
    }
}
