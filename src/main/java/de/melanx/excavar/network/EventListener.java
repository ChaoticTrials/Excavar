package de.melanx.excavar.network;

import de.melanx.excavar.Excavador;
import de.melanx.excavar.Excavar;
import de.melanx.excavar.PlayerHandler;
import net.minecraft.server.level.ServerPlayer;
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
                playerHandler.startDigging(playerId);
                Excavador excavador = new Excavador(event.getPos(), player.level, player);
                excavador.findBlocks(event.getPlayer().getMainHandItem());
                excavador.mine();
                playerHandler.stopDigging(playerId);
            }
        }
    }
}
