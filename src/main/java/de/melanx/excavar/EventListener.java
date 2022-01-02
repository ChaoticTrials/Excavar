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
            if (playerHandler.canDig(player)) {
                if (MinecraftForge.EVENT_BUS.post(new DiggingEvent.Pre((ServerLevel) player.level, player, Lists.newArrayList(), event.getState().getBlock()))) {
                    return;
                }

                Excavador excavador = new Excavador(event.getPos(), player.level, player);
                excavador.findBlocks();

                if (MinecraftForge.EVENT_BUS.post(new DiggingEvent.FoundPositions((ServerLevel) player.level, player, excavador.getBlocksToMine(), event.getState().getBlock()))) {
                    playerHandler.stopDigging(playerId);
                    return;
                }

                playerHandler.startDigging(playerId);
                excavador.mine(event.getPlayer().getMainHandItem());
                playerHandler.stopDigging(playerId);
                MinecraftForge.EVENT_BUS.post(new DiggingEvent.Post((ServerLevel) player.level, player, excavador.getBlocksToMine(), event.getState().getBlock()));
            }
        }
    }
}
