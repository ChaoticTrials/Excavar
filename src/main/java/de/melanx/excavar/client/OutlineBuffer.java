package de.melanx.excavar.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

// Thanks to
// https://github.com/castcrafter/travel_anchors/blob/0668947e17fac6dc5986a0b59c8fbc5b4e52021d/src/main/java/de/castcrafter/travel_anchors/render/OutlineBuffer.java
public class OutlineBuffer implements MultiBufferSource {

    public static final OutlineBuffer INSTANCE = new OutlineBuffer();

    private OutlineBuffer() {

    }

    @Nonnull
    @Override
    public VertexConsumer getBuffer(@NotNull RenderType type) {
        return Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(OutlineRenderType.get(type));
    }
}
