package de.melanx.excavar.network.handler;

import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.PlayerHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record KeyPress(UUID playerId, PressType pressType, PlayerHandler.ClientData data) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Excavar.MODID, "key_press");
    public static final CustomPacketPayload.Type<KeyPress> TYPE = new CustomPacketPayload.Type<>(ID);

    public KeyPress(UUID playerId, PressType pressType) {
        this(playerId, pressType, PlayerHandler.ClientData.EMPTY);
    }

    public static final StreamCodec<FriendlyByteBuf, KeyPress> CODEC = StreamCodec.of(
            KeyPress::encode, KeyPress::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            switch (this.pressType) {
                case PRESS, UPDATE -> Excavar.getPlayerHandler().putPlayer(this.playerId, this.data);
                case RELEASE -> Excavar.getPlayerHandler().removePlayer(this.playerId);
            }
        });
    }

    private static void encode(FriendlyByteBuf buffer, KeyPress msg) {
        buffer.writeUUID(msg.playerId);
        buffer.writeEnum(msg.pressType);
        buffer.writeBoolean(msg.data.requiresSneaking());
        buffer.writeBoolean(msg.data.preventToolBreaking());
        buffer.writeResourceLocation(msg.data.shapeId());
    }

    private static KeyPress decode(FriendlyByteBuf buffer) {
        return new KeyPress(buffer.readUUID(), buffer.readEnum(PressType.class), new PlayerHandler.ClientData(buffer.readBoolean(), buffer.readBoolean(), buffer.readResourceLocation()));
    }

    public enum PressType {
        PRESS,
        RELEASE,
        UPDATE
    }
}
