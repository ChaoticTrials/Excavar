package de.melanx.excavar.api.shape;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class Shapes {

    public static final Logger LOGGER = LogManager.getLogger(Shapes.class);
    private final static List<Identifier> allSelectableShapes = Lists.newArrayList();
    private final static List<Identifier> selectableShapes = Lists.newArrayList();
    private final static Map<Identifier, Shape> registry = Maps.newHashMap();
    public static final Identifier SHAPELESS = Identifier.fromNamespaceAndPath(Excavar.MODID, "shapeless");
    public static final Identifier EASY_SHAPELESS = Identifier.fromNamespaceAndPath(Excavar.MODID, "easy_shapeless");
    public static final Identifier PLANTS_SHAPELESS = Identifier.fromNamespaceAndPath(Excavar.MODID, "plants_shapeless");
    public static final Identifier TUNNEL = Identifier.fromNamespaceAndPath(Excavar.MODID, "tunnel");
    public static final Identifier BIG_TUNNEL = Identifier.fromNamespaceAndPath(Excavar.MODID, "big_tunnel");
    private static Identifier currentShape = SHAPELESS;

    private Shapes() {}

    /**
     * Used to register a custom {@link Shape}
     *
     * @see Shapes#register(Identifier, Shape, boolean)
     */
    public static void register(Identifier id, Shape shape) {
        register(id, shape, true);
    }

    /**
     * Used to register a custom {@link Shape}
     *
     * @param id         The {@link Identifier} linked to the {@link Shape} instance
     * @param shape      The {@link Shape} which will be registered
     * @param selectable Whether the {@link Shape} should be selectable
     */
    public static void register(Identifier id, Shape shape, boolean selectable) {
        if (registry.containsKey(id)) {
            throw new IllegalStateException("Shape '" + id + "' already registered.");
        }

        registry.put(id, shape);
        LOGGER.info("Registered shape '{}' - selectable {}", id, selectable ? "✅" : "❌");
        if (selectable) {
            allSelectableShapes.add(id);
        }
    }

    /**
     * @param id The registry name of the shape
     * @return A registered {@link Shape}.
     */
    public static Shape getShape(Identifier id) {
        if (!registry.containsKey(id)) {
            throw new IllegalArgumentException("Shape '" + id + "' was not registered.");
        }

        return registry.get(id);
    }

    /**
     * @return The next shape id in the list.
     */
    public static Identifier nextShapeId() {
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
    public static Identifier previousShapeId() {
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
    public static Identifier getSelectedShape() {
        return currentShape;
    }

    public static void refreshSelectableShapes() {
        selectableShapes.clear();
        selectableShapes.addAll(allSelectableShapes);
        selectableShapes.removeIf(id -> ConfigHandler.deniedShapes.get().contains(id.toString()));

        currentShape = selectableShapes.contains(currentShape) ? currentShape : selectableShapes.getFirst();
    }
}
