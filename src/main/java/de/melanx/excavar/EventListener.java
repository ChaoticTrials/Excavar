package de.melanx.excavar;

import com.google.common.collect.Lists;
import de.melanx.excavar.api.Excavador;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.api.events.DiggingEvent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

public class EventListener {

    @SubscribeEvent
    public void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            PlayerHandler playerHandler = Excavar.getPlayerHandler();
            UUID playerId = player.getGameProfile().getId();
            if (playerHandler.canDig(player)) {
                if (MinecraftForge.EVENT_BUS.post(new DiggingEvent.Pre((ServerWorld) player.world, player, Lists.newArrayList(), event.getState().getBlock()))) {
                    return;
                }

                Excavador excavador = new Excavador(event.getPos(), player.world, player);
                excavador.findBlocks();

                if (MinecraftForge.EVENT_BUS.post(new DiggingEvent.FoundPositions((ServerWorld) player.world, player, excavador.getBlocksToMine(), event.getState().getBlock()))) {
                    playerHandler.stopDigging(playerId);
                    return;
                }

                playerHandler.startDigging(playerId);
                excavador.mine(player.getHeldItemMainhand());
                playerHandler.stopDigging(playerId);
                MinecraftForge.EVENT_BUS.post(new DiggingEvent.Post((ServerWorld) player.world, player, excavador.getBlocksToMine(), event.getState().getBlock()));
            }
        }
    }
}
