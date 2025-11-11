package de.melanx.excavar;

import com.google.common.collect.Lists;
import de.melanx.excavar.api.Excavador;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.api.events.DiggingEvent;
import de.melanx.excavar.api.shape.Shapes;
import de.melanx.excavar.client.HiddenRenderTypes;
import de.melanx.excavar.config.ListHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.UUID;

@EventBusSubscriber(modid = Excavar.MODID)
public class EventListener {

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerHandler playerHandler = Excavar.getPlayerHandler();
            UUID playerId = player.getGameProfile().id();
            if (playerHandler.canDig(player)) {
                BlockState state = event.getState();
                if (!ShapeUtil.miningAllowed(state) || !ListHandler.isToolAllowed(player.getMainHandItem())) {
                    return;
                }

                ServerLevel level = player.level();
                if (NeoForge.EVENT_BUS.post(new DiggingEvent.Pre(level, player, Lists.newArrayList(), state.getBlock())).isCanceled()) {
                    return;
                }

                Direction side = ((BlockHitResult) player.pick(20, 0, false)).getDirection();

                ResourceLocation shapeId = playerHandler.getShapeId(playerId);
                if (!ConfigHandler.allowShapeSelection.get() || shapeId == Shapes.SHAPELESS) {
                    shapeId = ShapeUtil.getShapeId(state.getBlock());
                }

                Excavador excavador = new Excavador(shapeId, event.getPos(), level, player, side, state);
                excavador.findBlocks();

                if (NeoForge.EVENT_BUS.post(new DiggingEvent.FoundPositions(level, player, excavador.getBlocksToMine(), state.getBlock())).isCanceled()) {
                    playerHandler.stopDigging(playerId);
                    return;
                }

                playerHandler.startDigging(playerId);
                excavador.mine(event.getPlayer().getMainHandItem());
                playerHandler.stopDigging(playerId);
                NeoForge.EVENT_BUS.post(new DiggingEvent.Post(level, player, excavador.getBlocksToMine(), state.getBlock()));
            }
        }
    }

    @SubscribeEvent
    public static void register(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(HiddenRenderTypes.HIDDEN_RENDER_PIPELINE);
    }
}
