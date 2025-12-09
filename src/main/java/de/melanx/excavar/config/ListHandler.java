package de.melanx.excavar.config;

import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import de.melanx.excavar.api.ConfiguredSameTags;
import de.melanx.excavar.api.shape.Shapes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ListHandler {

    private static Set<Identifier> TOOL_DENY_LIST = null;

    private static void validate() {
        if (TOOL_DENY_LIST != null) {
            return;
        }

        TOOL_DENY_LIST = new HashSet<>();
        Set<Pattern> deniedTools = ConfigHandler.deniedTools.get().stream().map(s -> Pattern.compile("^" + s.replace("*", ".*") + "$")).collect(Collectors.toSet());
        for (Pattern regex : deniedTools) {
            Set<Identifier> itemIds = BuiltInRegistries.ITEM.keySet();
            for (int k = 0; k < itemIds.size(); k++) {
                Identifier id = (Identifier) itemIds.toArray()[k];
                if (id.toString().matches(regex.pattern())) {
                    TOOL_DENY_LIST.add(id);
                }
            }
        }
    }

    public static void onConfigChange(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (event instanceof ModConfigEvent.Unloading) {
            return;
        }

        if (config.getModId().equals(Excavar.MODID) && config.getType() == ModConfig.Type.SERVER) {
            TOOL_DENY_LIST = null;
            Shapes.refreshSelectableShapes();
            ConfiguredSameTags.refreshTags(ConfigHandler.groupedTags.get());
        }
    }

    public static boolean isToolAllowed(ItemStack stack) {
        return ListHandler.isToolAllowed(stack.getItem());
    }

    public static boolean isToolAllowed(Item item) {
        ListHandler.validate();
        return !TOOL_DENY_LIST.contains(BuiltInRegistries.ITEM.getKey(item));
    }
}
