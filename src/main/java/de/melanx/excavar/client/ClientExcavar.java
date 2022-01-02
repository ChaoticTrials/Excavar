package de.melanx.excavar.client;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.PlayerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;

public class ClientExcavar {

    public static final KeyBinding EXCAVAR = new KeyBinding(Excavar.MODID + ".key.excavar", GLFW.GLFW_KEY_LEFT_ALT, "Excavar");

    public ClientExcavar() {
        ClientRegistry.registerKeyBinding(EXCAVAR);
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event) {
        if (event.getKey() == EXCAVAR.getKey().getKeyCode()) {
            ClientExcavar.handleInput(event.getAction());
        }
    }

    @SubscribeEvent
    public void mouseInput(InputEvent.MouseInputEvent event) {
        if (event.getButton() == EXCAVAR.getKey().getKeyCode()) {
            ClientExcavar.handleInput(event.getAction());
        }
    }

    private static void handleInput(int action) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (action == GLFW.GLFW_PRESS) {
            Excavar.getNetwork().press(player, new PlayerHandler.ClientData(ClientConfig.onlyWhileSneaking.get(), ClientConfig.preventToolsBreaking.get()));
        } else if (action == GLFW.GLFW_RELEASE) {
            Excavar.getNetwork().release(player);
        }
    }
}
