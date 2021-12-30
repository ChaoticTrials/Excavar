package de.melanx.excavar.network;

import net.minecraft.network.FriendlyByteBuf;

// [LibX copy]
// https://github.com/noeppi-noeppi/LibX/blob/405e75973b247cd3374a420b4127bb59d28417d5/src/main/java/io/github/noeppi_noeppi/libx/network/PacketSerializer.java
public interface PacketSerializer<T> {

    Class<T> messageClass();

    void encode(T msg, FriendlyByteBuf buffer);

    T decode(FriendlyByteBuf buffer);
}
