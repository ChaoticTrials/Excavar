package de.melanx.excavar.api.shape;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.melanx.excavar.Excavar;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class Shapes {

    private final static List<ResourceLocation> selectableShapes = Lists.newArrayList();
    private final static Map<ResourceLocation, Shape> registry = Maps.newHashMap();
    public static final ResourceLocation SHAPELESS = new ResourceLocation(Excavar.MODID, "shapeless");
    public static final ResourceLocation EASY_SHAPELESS = new ResourceLocation(Excavar.MODID, "easy_shapeless");
    public static final ResourceLocation PLANTS_SHAPELESS = new ResourceLocation(Excavar.MODID, "plants_shapeless");
    public static final ResourceLocation TUNNEL = new ResourceLocation(Excavar.MODID, "tunnel");
    private static ResourceLocation currentShape = SHAPELESS;

    private Shapes() {
    }

    /**
     * Used to register a custom {@link Shape}
     *
     * @see Shapes#register(ResourceLocation, Shape, boolean)
     */
    public static void register(ResourceLocation id, Shape shape) {
        register(id, shape, true);
    }

    /**
     * Used to register a custom {@link Shape}
     *
     * @param id         The {@link ResourceLocation} linked to the {@link Shape} instance
     * @param shape      The {@link Shape} which will be registered
     * @param selectable Whether the {@link Shape} should be selectable
     */
    public static void register(ResourceLocation id, Shape shape, boolean selectable) {
        if (registry.containsKey(id)) {
            throw new IllegalStateException("Shape '" + id + "' already registered.");
        }

        registry.put(id, shape);
        if (selectable) {
            selectableShapes.add(id);
        }
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

    /**
     * @return The next shape id in the list.
     */
    public static ResourceLocation nextShapeId() {
        int i = selectableShapes.indexOf(currentShape);
        if (i >= selectableShapes.size() - 1) {
            i = 0;
        } else {
            i++;
        }

        currentShape = selectableShapes.get(i);
        return currentShape;
    }

    /**
     * @return The previous shape id in the list.
     */
    public static ResourceLocation previousShapeId() {
        int i = selectableShapes.indexOf(currentShape);
        if (i == 0) {
            i = selectableShapes.size() - 1;
        } else {
            i--;
        }

        currentShape = selectableShapes.get(i);
        return currentShape;
    }

    /**
     * @return The current selected shape id.
     */
    public static ResourceLocation getSelectedShape() {
        return currentShape;
    }
}
