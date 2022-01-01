package de.melanx.excavar.network;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.network.handler.KeyPress;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

// highly inspired by LibX
// https://github.com/noeppi-noeppi/LibX/blob/405e75973b247cd3374a420b4127bb59d28417d5/src/main/java/io/github/noeppi_noeppi/libx/network/NetworkX.java
public class DiggingNetwork {

    private static final Object LOCK = new Object();
    private static final String NET_VERSION = "1.2";

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

    public void press(UUID id, PlayerHandler.ClientData data) {
        this.channel.sendToServer(new KeyPress.Message(id, KeyPress.Type.PRESSED, data));
    }

    public void release(UUID id) {
        this.channel.sendToServer(new KeyPress.Message(id, KeyPress.Type.NOT_PRESSED, PlayerHandler.ClientData.EMPTY));
    }

    public void registerPackets() {
        this.register(new KeyPress.Serializer(), () -> KeyPress::handle, NetworkDirection.PLAY_TO_SERVER);
    }

    protected <T> void register(PacketSerializer<T> serializer, Supplier<BiConsumer<T, Supplier<NetworkEvent.Context>>> handler, @Nonnull NetworkDirection direction) {
        synchronized (LOCK) {
            Objects.requireNonNull(direction);
            BiConsumer<T, Supplier<NetworkEvent.Context>> realHandler;
            if (direction == NetworkDirection.PLAY_TO_CLIENT || direction == NetworkDirection.LOGIN_TO_CLIENT) {
                realHandler = DistExecutor.unsafeRunForDist(() -> handler, () -> () -> (msg, ctx) -> {
                });
            } else {
                realHandler = handler.get();
            }
            this.channel.registerMessage(this.id++, serializer.messageClass(), serializer::encode, serializer::decode, realHandler, Optional.of(direction));
        }
    }
}
