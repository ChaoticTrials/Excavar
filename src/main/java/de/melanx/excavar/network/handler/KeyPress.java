package de.melanx.excavar.network.handler;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.network.PacketSerializer;
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
                case PRESSED -> Excavar.getPlayerHandler().addPlayer(msg.playerId, msg.requiresSneaking);
                case NOT_PRESSED -> Excavar.getPlayerHandler().removePlayer(msg.playerId);
            }
        });
        ctx.setPacketHandled(true);
    }

    public static class Serializer implements PacketSerializer<Message> {

        @Override
        public Class<Message> messageClass() {
            return Message.class;
        }

        @Override
        public void encode(Message msg, FriendlyByteBuf buffer) {
            buffer.writeUUID(msg.playerId);
            buffer.writeEnum(msg.type);
            buffer.writeBoolean(msg.requiresSneaking);
        }

        @Override
        public Message decode(FriendlyByteBuf buffer) {
            return new Message(buffer.readUUID(), buffer.readEnum(Type.class), buffer.readBoolean());
        }
    }

    public record Message(UUID playerId, Type type, boolean requiresSneaking) {

    }

    public enum Type {
        PRESSED,
        NOT_PRESSED
    }
}
