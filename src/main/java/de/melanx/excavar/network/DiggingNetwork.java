package de.melanx.excavar.network;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.network.handler.KeyPress;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

// highly inspired by LibX
// https://github.com/noeppi-noeppi/LibX/blob/405e75973b247cd3374a420b4127bb59d28417d5/src/main/java/io/github/noeppi_noeppi/libx/network/NetworkX.java
public class DiggingNetwork {

    private static final String NET_VERSION = "1.5";

    private final SimpleChannel channel;
    private int id = 0;

    public DiggingNetwork() {
        this.channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Excavar.MODID, "netchannel"),
                () -> NET_VERSION,
                // allow joining if mod is not installed on client/server
                s -> NET_VERSION.equals(s) || NetworkRegistry.ACCEPTVANILLA.equals(s),
                s -> NET_VERSION.equals(s) || NetworkRegistry.ACCEPTVANILLA.equals(s)
        );
    }

    public void press(Player player, PlayerHandler.ClientData data) {
        if (player instanceof LocalPlayer localPlayer && this.channel.isRemotePresent(localPlayer.connection.getConnection())) {
            this.channel.sendToServer(new KeyPress.Message(player.getGameProfile().getId(), KeyPress.Type.PRESS, data));
        }
    }

    public void release(Player player) {
        if (player instanceof LocalPlayer localPlayer && this.channel.isRemotePresent(localPlayer.connection.getConnection())) {
            this.channel.sendToServer(new KeyPress.Message(player.getGameProfile().getId(), KeyPress.Type.RELEASE));
        }
    }

    public void update(Player player, PlayerHandler.ClientData data) {
        if (player instanceof LocalPlayer localPlayer && this.channel.isRemotePresent(localPlayer.connection.getConnection())) {
            this.channel.sendToServer(new KeyPress.Message(player.getGameProfile().getId(), KeyPress.Type.UPDATE, data));
        }
    }

    public void registerPackets() {
        KeyPress.Serializer serializer = new KeyPress.Serializer();
        this.channel.registerMessage(this.id++, KeyPress.Message.class, serializer::encode, serializer::decode, KeyPress::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
