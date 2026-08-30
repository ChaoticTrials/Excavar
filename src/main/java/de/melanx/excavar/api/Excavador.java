package de.melanx.excavar.api;

import com.google.common.collect.Lists;
import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.shape.Shape;
import de.melanx.excavar.api.shape.Shapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Excavador {

    public static final TagKey<Block> FORBIDDEN_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("excavar", "forbidden_blocks"));

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
        this(Shapes.getSelectedShape(), start, level, player, side, originalState, requiresCorrectTool, Excavar.getPlayerHandler().getData(player.getGameProfile().id()).preventToolBreaking());
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
        this(shapeId, start, level, player, side, originalState, requiresCorrectTool, Excavar.getPlayerHandler().getData(player.getGameProfile().id()).preventToolBreaking());
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
        limit--;

        // find only start block when it's not the correct tool, but it's required via config
        if (this.cannotMine()) {
            return;
        }

        if (ConfigHandler.invertForbiddenTag.get() != this.level.getBlockState(this.start).is(FORBIDDEN_BLOCKS)) {
            return;
        }

        List<BlockPos> collectedBlocks = new ArrayList<>(this.blocksToMine);
        this.shape.addNeighbors(this.level, this.player, this.start.mutable(), this.side.getOpposite(), this.originalState, collectedBlocks, limit);

        collectedBlocks.remove(this.start);
        int canAdd = Math.min(limit, collectedBlocks.size());
        this.blocksToMine.addAll(collectedBlocks.subList(0, canAdd));
    }

    /**
     * Mines all the blocks
     *
     * @param tool The tool which will be used to mine all the blocks
     */
    public void mine(ItemStack tool) {
        int totalPlayerXp = Excavador.getExpPoints(this.player.experienceLevel, this.player.experienceProgress);
        int stopAt = this.preventToolBreaking ? 2 : 1; // we need to increase this by 1, otherwise the tool will be broken too early
        if (!(this.player instanceof ServerPlayer serverPlayer)) {
            throw new IllegalStateException("Can't mine on client side");
        }

        int i = 0;
        boolean payedXp = false;
        for (BlockPos pos : this.blocksToMine) {
            if (pos.equals(this.start)) {
                i++;
                continue;
            }

            boolean xpUsageRequirement = switch(ConfigHandler.xpUsageType.get()) {
                case PER_BLOCK -> i >= 1;
                case PER_ACTION -> i == 1 || !payedXp;
            };
            boolean tooLessXp = xpUsageRequirement && totalPlayerXp - ConfigHandler.xpUsage.get() < 0;
            if ((tool.isDamageableItem() && tool.getMaxDamage() - tool.getDamageValue() <= stopAt || tooLessXp) && !serverPlayer.isCreative()) {
                if (tooLessXp) {
                    serverPlayer.sendSystemMessage(Component.translatable("excavar.config.xp_usage.missing", this.blocksToMine.size() - i - ConfigHandler.xpUsage.get()));
                }

                break;
            }

            if (serverPlayer.gameMode.destroyBlock(pos)) {
                serverPlayer.causeFoodExhaustion((float) (ConfigHandler.hungerUsage.get() - 0.005F));

                // prevent xp usage for first block
                if (xpUsageRequirement) {
                    payedXp = true;
                    serverPlayer.giveExperiencePoints(-ConfigHandler.xpUsage.get());
                    totalPlayerXp -= ConfigHandler.xpUsage.get();
                }
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

    private boolean cannotMine() {
        BlockState state = this.level.getBlockState(this.start);
        boolean forbiddenFist = ConfigHandler.fistForbidden.get()
                && this.player.getMainHandItem().isEmpty()
                && state.getDestroySpeed(this.level, this.start) > 0;

        if (!this.requiresCorrectTool) {
            return forbiddenFist;
        }

        boolean cannotHarvest = !EventHooks.doPlayerHarvestCheck(
                this.player, state, this.level, this.start);

        ItemStack mainHandItem = this.player.getMainHandItem();
        boolean toolTagMatches = this.toolTagMatchesBlock(mainHandItem, state);

        return !toolTagMatches || cannotHarvest || forbiddenFist;
    }

    private boolean toolTagMatchesBlock(ItemStack stack, BlockState state) {
        boolean isAxe = stack.is(ItemTags.AXES);
        boolean isHoe = stack.is(ItemTags.HOES);
        boolean isPickaxe = stack.is(ItemTags.PICKAXES);
        boolean isShovel = stack.is(ItemTags.SHOVELS);
        boolean isSword = stack.is(ItemTags.SWORDS);

        boolean blockNeedsAxe = state.is(BlockTags.MINEABLE_WITH_AXE);
        boolean blockNeedsHoe = state.is(BlockTags.MINEABLE_WITH_HOE);
        boolean blockNeedsPickaxe = state.is(BlockTags.MINEABLE_WITH_PICKAXE);
        boolean blockNeedsShovel = state.is(BlockTags.MINEABLE_WITH_SHOVEL);
        boolean blockNeedsSword = state.is(BlockTags.SWORD_EFFICIENT) || state.is(BlockTags.SWORD_INSTANTLY_MINES);

        if (isAxe && blockNeedsAxe) return true;
        if (isHoe && blockNeedsHoe) return true;
        if (isPickaxe && blockNeedsPickaxe) return true;
        if (isShovel && blockNeedsShovel) return true;
        if (isSword && blockNeedsSword) return true;

        return !(blockNeedsAxe || blockNeedsHoe || blockNeedsPickaxe || blockNeedsShovel || blockNeedsSword);
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
