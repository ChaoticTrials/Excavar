package de.melanx.excavar.network.handler;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.PlayerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class KeyPress {

    public static void handle(Message msg, Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getSender() == null) {
                return;
            }

            switch (msg.type) {
                case PRESS, UPDATE -> Excavar.getPlayerHandler().putPlayer(msg.playerId, msg.data);
                case RELEASE -> Excavar.getPlayerHandler().removePlayer(msg.playerId);
            }
        });
        ctx.setPacketHandled(true);
    }

    public static class Serializer {

        public void encode(Message msg, FriendlyByteBuf buffer) {
            buffer.writeUUID(msg.playerId);
            buffer.writeEnum(msg.type);
            buffer.writeBoolean(msg.data.requiresSneaking());
            buffer.writeBoolean(msg.data.preventToolBreaking());
            buffer.writeResourceLocation(msg.data.shapeId());
        }

        public Message decode(FriendlyByteBuf buffer) {
            return new Message(buffer.readUUID(), buffer.readEnum(Type.class), new PlayerHandler.ClientData(buffer.readBoolean(), buffer.readBoolean(), buffer.readResourceLocation()));
        }
    }

    public record Message(UUID playerId, Type type, PlayerHandler.ClientData data) {

        public Message(UUID playerId, Type type) {
            this(playerId, type, PlayerHandler.ClientData.EMPTY);
        }
    }

    public enum Type {
        PRESS,
        RELEASE,
        UPDATE
    }
}
