package de.melanx.excavar.network;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.network.handler.KeyPress;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class DiggingNetwork {

    public static void onRegisterPayloadHandler(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Excavar.MODID)
                .versioned("1.0")
                .optional();

        registrar.playToServer(KeyPress.TYPE, KeyPress.CODEC, KeyPress::handle);
    }

    public static void press(Player player, PlayerHandler.ClientData data) {
        if (player instanceof LocalPlayer) {
            PacketDistributor.sendToServer(new KeyPress(player.getGameProfile().getId(), KeyPress.PressType.PRESS, data));
        }
    }

    public static void release(Player player) {
        if (player instanceof LocalPlayer) {
            PacketDistributor.sendToServer(new KeyPress(player.getGameProfile().getId(), KeyPress.PressType.RELEASE));
        }
    }

    public static void update(Player player, PlayerHandler.ClientData data) {
        if (player instanceof LocalPlayer) {
            PacketDistributor.sendToServer(new KeyPress(player.getGameProfile().getId(), KeyPress.PressType.UPDATE, data));
        }
    }
}
