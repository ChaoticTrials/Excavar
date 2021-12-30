package de.melanx.excavar;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class ClientExcavar {

    public static final KeyMapping EXCAVAR = new KeyMapping(Excavar.MODID + ".key.excavar", GLFW.GLFW_KEY_LEFT_ALT, "Excavar");

    public ClientExcavar() {
        ClientRegistry.registerKeyBinding(EXCAVAR);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event) {
        if (event.getKey() == EXCAVAR.getKey().getValue() && Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            if (event.getAction() == GLFW.GLFW_PRESS) {
                Excavar.getNetwork().press(player.getGameProfile().getId());
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                Excavar.getNetwork().release(player.getGameProfile().getId());
            }
        }
    }

    @SubscribeEvent
    public void mouseInput(InputEvent.MouseInputEvent event) {
        if (event.getButton() == EXCAVAR.getKey().getValue() && Minecraft.getInstance().player != null) {
            Player player = Minecraft.getInstance().player;
            if (event.getAction() == GLFW.GLFW_PRESS) {
                Excavar.getNetwork().press(player.getGameProfile().getId());
            } else if (event.getAction() == GLFW.GLFW_RELEASE) {
                Excavar.getNetwork().release(player.getGameProfile().getId());
            }
        }
    }
}
