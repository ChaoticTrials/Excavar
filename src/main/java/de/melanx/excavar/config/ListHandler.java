package de.melanx.excavar.config;

import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ListHandler {

    private static Set<ResourceLocation> TOOL_DENY_LIST = null;

    private static void validate() {
        if (TOOL_DENY_LIST != null) {
            return;
        }

        TOOL_DENY_LIST = new HashSet<>();
        Set<Pattern> deniedTools = ConfigHandler.deniedTools.get().stream().map(s -> Pattern.compile("^" + s.replace("*", ".*") + "$")).collect(Collectors.toSet());
        for (Pattern regex : deniedTools) {
            Set<ResourceLocation> itemIds = BuiltInRegistries.ITEM.keySet();
            for (int k = 0; k < itemIds.size(); k++) {
                ResourceLocation id = (ResourceLocation) itemIds.toArray()[k];
                if (id.toString().matches(regex.pattern())) {
                    TOOL_DENY_LIST.add(id);
                }
            }
        }
    }

    public static void onConfigChange(ModConfigEvent event) {
        if (event.getConfig().getModId().equals(Excavar.MODID)) {
            TOOL_DENY_LIST = null;
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
