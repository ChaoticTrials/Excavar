package de.melanx.excavar.client;

import de.melanx.excavar.Excavar;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.lwjgl.glfw.GLFW;

public class ClientExcavar {

    public static final KeyMapping EXCAVAR = new KeyMapping(Excavar.MODID + ".key.excavar", GLFW.GLFW_KEY_LEFT_ALT, "Excavar");

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

    private static void handleInput(int action) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (action == GLFW.GLFW_PRESS) {
            Excavar.getNetwork().press(player.getGameProfile().getId(), ClientConfig.onlyWhileSneaking.get());
        } else if (action == GLFW.GLFW_RELEASE) {
            Excavar.getNetwork().release(player.getGameProfile().getId());
        }
    }
}
