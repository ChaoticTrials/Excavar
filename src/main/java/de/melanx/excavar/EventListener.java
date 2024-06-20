package de.melanx.excavar;

import com.google.common.collect.Lists;
import de.melanx.excavar.api.Excavador;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.api.events.DiggingEvent;
import de.melanx.excavar.api.shape.Shapes;
import de.melanx.excavar.config.ListHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.UUID;

public class EventListener {

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
                if (!ConfigHandler.allowShapeSelection.get() || shapeId == Shapes.SHAPELESS) {
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
                NeoForge.EVENT_BUS.post(new DiggingEvent.Post((ServerLevel) level, player, excavador.getBlocksToMine(), state.getBlock()));
            }
        }
    }
}
