package de.melanx.excavar;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.melanx.excavar.api.Excavador;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.api.events.DiggingEvent;
import de.melanx.excavar.api.shape.Shapes;
import de.melanx.excavar.config.ListHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventListener {

    public static Map<UUID, List<ItemEntity>> ITEMS_FOR_PLAYER = Maps.newHashMap();

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerHandler playerHandler = Excavar.getPlayerHandler();
            UUID playerId = player.getGameProfile().getId();
            if (playerHandler.canDig(player)) {
                BlockState state = event.getState();
                if (!ShapeUtil.miningAllowed(state) || !ListHandler.isToolAllowed(player.getMainHandItem())) {
                    return;
                }

                Level level = player.level();
                if (NeoForge.EVENT_BUS.post(new DiggingEvent.Pre((ServerLevel) level, player, Lists.newArrayList(), state.getBlock())).isCanceled()) {
                    return;
                }

                Direction side = ((BlockHitResult) player.pick(20, 0, false)).getDirection();

                ResourceLocation shapeId = playerHandler.getShapeId(playerId);
                if (!ConfigHandler.allowShapeSelection.get() || Shapes.SHAPELESS.equals(shapeId)) {
                    shapeId = ShapeUtil.getShapeId(state.getBlock());
                }

                Excavador excavador = new Excavador(shapeId, event.getPos(), level, player, side, state);
                excavador.findBlocks();

                if (NeoForge.EVENT_BUS.post(new DiggingEvent.FoundPositions((ServerLevel) level, player, excavador.getBlocksToMine(), state.getBlock())).isCanceled()) {
                    playerHandler.stopDigging(playerId);
                    return;
                }

                playerHandler.startDigging(playerId);
                excavador.mine(event.getPlayer().getMainHandItem());
                playerHandler.stopDigging(playerId);

                if (ITEMS_FOR_PLAYER.containsKey(playerId)) {
                    ITEMS_FOR_PLAYER.get(playerId).forEach(e -> {
                        e.setPos(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
                        level.addFreshEntity(e);
                    });
                    ITEMS_FOR_PLAYER.remove(playerId);
                }

                NeoForge.EVENT_BUS.post(new DiggingEvent.Post((ServerLevel) level, player, excavador.getBlocksToMine(), state.getBlock()));
            }
        }
    }

    @SubscribeEvent
    public void captureDrops(BlockDropsEvent event) {
        if (!ConfigHandler.collectDrops.get()) {
            return;
        }

        if (event.isCanceled()) {
            return;
        }

        if (event.getBreaker() instanceof ServerPlayer player && Excavar.getPlayerHandler().isDigging(player.getGameProfile().getId())) {
            event.setCanceled(true);
            ITEMS_FOR_PLAYER.computeIfAbsent(player.getGameProfile().getId(), id -> new ArrayList<>()).addAll(event.getDrops());
        }
    }
}
