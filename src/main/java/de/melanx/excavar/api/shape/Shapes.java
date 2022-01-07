package de.melanx.excavar.api.shape;

import com.google.common.collect.Maps;
import de.melanx.excavar.Excavar;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class Shapes {

    private final static Map<ResourceLocation, Shape> registry = Maps.newHashMap();
    public static final ResourceLocation SHAPELESS = new ResourceLocation(Excavar.MODID, "shapeless");
    public static final ResourceLocation EASY_SHAPELESS = new ResourceLocation(Excavar.MODID, "easy_shapeless");
    public static final ResourceLocation PLANTS_SHAPELESS = new ResourceLocation(Excavar.MODID, "plants_shapeless");
    public static final ResourceLocation TUNNEL = new ResourceLocation(Excavar.MODID, "tunnel");

    private Shapes() {
    }

    /**
     * Used to register a custom {@link Shape}
     *
     * @param id    The {@link ResourceLocation} linked to the {@link Shape} instance
     * @param shape The {@link Shape} which will be registered
     */
    public static void register(ResourceLocation id, Shape shape) {
        if (registry.containsKey(id)) {
            throw new IllegalStateException("Shape '" + id + "' already registered.");
        }

        registry.put(id, shape);
    }

    /**
     * @param id The registry name of the shape
     * @return A registered {@link Shape}.
     */
    public static Shape getShape(ResourceLocation id) {
        if (!registry.containsKey(id)) {
            throw new IllegalArgumentException("Shape '" + id + "' was not registered.");
        }

        return registry.get(id);
    }
}
