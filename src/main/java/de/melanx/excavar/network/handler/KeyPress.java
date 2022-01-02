package de.melanx.excavar.network.handler;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.PlayerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class KeyPress {

    public static void handle(Message msg, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getSender() == null) {
                return;
            }

            if (msg.pressed) {
                Excavar.getPlayerHandler().addPlayer(msg.playerId, msg.data);
            } else {
                Excavar.getPlayerHandler().removePlayer(msg.playerId);
            }
        });
        ctx.setPacketHandled(true);
    }

    public static class Serializer {

        public void encode(Message msg, FriendlyByteBuf buffer) {
            buffer.writeUUID(msg.playerId);
            buffer.writeBoolean(msg.pressed);
            buffer.writeBoolean(msg.data.requiresSneaking());
            buffer.writeBoolean(msg.data.preventToolBreaking());
        }

        public Message decode(FriendlyByteBuf buffer) {
            return new Message(buffer.readUUID(), buffer.readBoolean(), new PlayerHandler.ClientData(buffer.readBoolean(), buffer.readBoolean()));
        }
    }

    public record Message(UUID playerId, boolean pressed, PlayerHandler.ClientData data) {
    }
}
