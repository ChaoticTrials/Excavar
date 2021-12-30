package de.melanx.excavar;

import com.google.common.collect.Lists;
import de.melanx.excavar.api.Excavador;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.api.events.DiggingEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class EventListener {

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerHandler playerHandler = Excavar.getPlayerHandler();
            UUID playerId = player.getGameProfile().getId();
            if (playerHandler.canDig(playerId)) {
                DiggingEvent.Pre preEvent = new DiggingEvent.Pre((ServerLevel) player.level, player, Lists.newArrayList(), event.getState().getBlock());
                if (preEvent.isCanceled()) {
                    return;
                }

                playerHandler.startDigging(playerId);
                Excavador excavador = new Excavador(event.getPos(), player.level, player);
                excavador.findBlocks(event.getPlayer().getMainHandItem());

                DiggingEvent.FoundPositions foundPositionsEvent = new DiggingEvent.FoundPositions((ServerLevel) player.level, player, excavador.getBlocksToMine(), event.getState().getBlock());
                if (foundPositionsEvent.isCanceled()) {
                    playerHandler.stopDigging(playerId);
                    return;
                }

                MinecraftForge.EVENT_BUS.post(foundPositionsEvent);
                excavador.mine();
                playerHandler.stopDigging(playerId);
                System.out.println(excavador.getBlocksToMine().size());
                MinecraftForge.EVENT_BUS.post(new DiggingEvent.Post((ServerLevel) player.level, player, excavador.getBlocksToMine(), event.getState().getBlock()));
            }
        }
    }
}
