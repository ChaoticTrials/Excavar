package de.melanx.excavar.client;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.ShapeUtil;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.config.ListHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.DrawSelectionEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class ClientExcavar {

    public static final KeyMapping EXCAVAR = new KeyMapping(Excavar.MODID + ".key.excavar", GLFW.GLFW_KEY_LEFT_ALT, "Excavar");
    private BlockHighlighter blockHighlighter = null;
    private Matcher matcher = new Matcher(BlockPos.ZERO, null, Blocks.AIR.defaultBlockState());

    public ClientExcavar() {
        ClientRegistry.registerKeyBinding(EXCAVAR);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event) {
        if (event.getKey() == EXCAVAR.getKey().getValue()) {
            ClientExcavar.handleInput(event.getAction());
        }
    }

    @SubscribeEvent
    public void mouseInput(InputEvent.MouseInputEvent event) {
        if (event.getButton() == EXCAVAR.getKey().getValue()) {
            ClientExcavar.handleInput(event.getAction());
        }
    }

    @SubscribeEvent
    public void renderBlockHighlights(DrawSelectionEvent.HighlightBlock event) {
        LocalPlayer player = Minecraft.getInstance().player;
        //noinspection ConstantConditions
        if (!ClientConfig.enableOutline.get() || !ListHandler.isToolAllowed(player.getMainHandItem())) {
            return;
        }

        BlockHitResult hitResult = event.getTarget();
        BlockState state = player.level.getBlockState(hitResult.getBlockPos());

        if (!ShapeUtil.miningAllowed(state)) {
            return;
        }

        if (EXCAVAR.isDown() && (player.isShiftKeyDown() || !ClientConfig.onlyWhileSneaking.get())) {
            if (!this.matcher.matches(hitResult.getBlockPos(), hitResult.getDirection(), state) || this.blockHighlighter == null) {
                this.blockHighlighter = new BlockHighlighter(hitResult);
                this.matcher = new Matcher(hitResult.getBlockPos(), hitResult.getDirection(), state);
            }
            this.blockHighlighter.render(event.getLevelRenderer(), event.getPoseStack());
        } else {
            this.blockHighlighter = null;
        }
    }

    private static void handleInput(int action) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (action == GLFW.GLFW_PRESS) {
            PlayerHandler.ClientData data = new PlayerHandler.ClientData(ClientConfig.onlyWhileSneaking.get(), ClientConfig.preventToolsBreaking.get());
            Excavar.getNetwork().press(player, data);
            Excavar.getPlayerHandler().addPlayer(player.getGameProfile().getId(), data);
        } else if (action == GLFW.GLFW_RELEASE) {
            Excavar.getNetwork().release(player);
            Excavar.getPlayerHandler().removePlayer(player.getGameProfile().getId());
        }
    }

    private record Matcher(@Nonnull BlockPos pos, @Nonnull Direction side, @Nonnull BlockState state) {

        public boolean matches(BlockPos otherPos, Direction /* hello from the */ otherSide, BlockState state) {
            return this.pos.equals(otherPos) && this.side == otherSide && this.state.equals(state);
        }
    }
}
