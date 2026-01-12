package de.melanx.excavar.config;

import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.ConfiguredSameTags;
import de.melanx.excavar.api.shape.Shapes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ListHandler {

    private static Pattern FORBIDDEN_TOOLS = null;

    private static void validate() {
        if (FORBIDDEN_TOOLS != null) {
            return;
        }

        if (ConfigHandler.deniedTools.get().isEmpty()) {
            FORBIDDEN_TOOLS = Pattern.compile("(?!)");
            return;
        }

        String regex = ConfigHandler.deniedTools.get().stream()
                .map(s -> s.replace("*", ".*"))
                .collect(Collectors.joining("|", "^(", ")$"));

        FORBIDDEN_TOOLS = Pattern.compile(regex);
    }

    public static void onConfigChange(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (event instanceof ModConfigEvent.Unloading) {
            return;
        }

        if (config.getModId().equals(Excavar.MODID) && config.getType() == ModConfig.Type.SERVER) {
            FORBIDDEN_TOOLS = null;
            Shapes.refreshSelectableShapes();
            ConfiguredSameTags.refreshTags(ConfigHandler.groupedTags.get());
        }
    }

    public static boolean isToolAllowed(ItemStack stack) {
        return ListHandler.isToolAllowed(stack.getItem());
    }

    public static boolean isToolAllowed(Item item) {
        ListHandler.validate();
        return !FORBIDDEN_TOOLS.matcher(BuiltInRegistries.ITEM.getKey(item).toString()).matches();
    }
}
