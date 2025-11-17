package de.melanx.excavar.client;

import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.api.shape.Shapes;
import de.melanx.excavar.network.DiggingNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

@Mod(value = Excavar.MODID, dist = Dist.CLIENT)
public class ClientExcavar {

    public static final KeyMapping.Category EXCAVAR_KEYS = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(Excavar.MODID, "keys"));
    public static final KeyMapping EXCAVAR = new KeyMapping(Excavar.MODID + ".key.excavar", GLFW.GLFW_KEY_LEFT_ALT, EXCAVAR_KEYS);

    public ClientExcavar(IEventBus bus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        bus.addListener(this::onRegisterKeys);
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    public void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.registerCategory(EXCAVAR_KEYS);
        event.register(EXCAVAR);
    }

    @SubscribeEvent
    public void keyInput(InputEvent.Key event) {
        if (event.getKey() == EXCAVAR.getKey().getValue()) {
            ClientExcavar.handleInput(event.getAction());
        }

        if (EXCAVAR.isDown() && Minecraft.getInstance().hasShiftDown() && Minecraft.getInstance().player != null) {
            ClientExcavar.displayShapeSelection(Minecraft.getInstance().player);
        }
    }

    @SubscribeEvent
    public void mouseInput(InputEvent.MouseButton.Pre event) {
        if (event.getButton() == EXCAVAR.getKey().getValue()) {
            ClientExcavar.handleInput(event.getAction());
        }
    }

    @SubscribeEvent
    public void mouseScroll(InputEvent.MouseScrollingEvent event) {
        if (!ConfigHandler.allowShapeSelection.get()) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (EXCAVAR.isDown() && Minecraft.getInstance().hasShiftDown() && player != null && Minecraft.getInstance().screen == null) {
            ResourceLocation prevId = Shapes.getSelectedShape();
            ResourceLocation id;
            if (event.getScrollDeltaY() > 0) {
                id = Shapes.previousShapeId();
            } else {
                id = Shapes.nextShapeId();
            }

            if (prevId != id) {
                PlayerHandler.ClientData data = new PlayerHandler.ClientData(ClientConfig.onlyWhileSneaking.get(), ClientConfig.preventToolsBreaking.get(), id);
                DiggingNetwork.update(player, data);
                Excavar.getPlayerHandler().putPlayer(player.getGameProfile().id(), data);
                ClientExcavar.displayShapeSelection(player);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void renderBlockHighlights(ExtractBlockOutlineRenderStateEvent event) {
        event.addCustomRenderer(new BlockRenderer());
    }

    private static void handleInput(int action) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (action == GLFW.GLFW_PRESS) {
            PlayerHandler.ClientData data = new PlayerHandler.ClientData(ClientConfig.onlyWhileSneaking.get(), ClientConfig.preventToolsBreaking.get(), Shapes.getSelectedShape());
            DiggingNetwork.press(player, data);
            Excavar.getPlayerHandler().putPlayer(player.getGameProfile().id(), data);
        } else if (action == GLFW.GLFW_RELEASE) {
            DiggingNetwork.release(player);
            Excavar.getPlayerHandler().removePlayer(player.getGameProfile().id());
        }
    }

    private static void displayShapeSelection(Player player) {
        ResourceLocation id = Shapes.getSelectedShape();
        MutableComponent msg = Component.translatable("excavar.shape.selected");
        msg.append(Component.translatable(id.getNamespace() + ".shape." + id.getPath().replace("/", ".") + ".desc").withStyle(ChatFormatting.GOLD));
        player.displayClientMessage(msg, true);
    }
}
