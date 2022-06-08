package de.melanx.excavar.config;

import com.google.common.collect.Sets;
import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.regex.Pattern;

public class ListHandler {

    private static final Set<ResourceLocation> TOOL_DENY_LIST = Sets.newHashSet();

    public static void refreshLists() {
        String[] deniedTools = ConfigHandler.deniedTools.get().toArray(new String[0]);
        TOOL_DENY_LIST.clear();

        for (String s : deniedTools) {
            if (s.contains("*")) {
                Pattern regex = Pattern.compile("^" + s.replace("*", ".*") + "$");
                Set<ResourceLocation> itemIds = ForgeRegistries.ITEMS.getKeys();
                for (int k = 0; k < itemIds.size(); k++) {
                    ResourceLocation id = (ResourceLocation) itemIds.toArray()[k];
                    if (id.toString().matches(regex.pattern())) {
                        TOOL_DENY_LIST.add(id);
                    }
                }
            } else {
                ResourceLocation id = ResourceLocation.tryParse(s);
                if (id == null) {
                    Excavar.LOGGER.warn("Invalid id: " + s);
                } else {
                    TOOL_DENY_LIST.add(id);
                }
            }
        }
    }

    public static boolean isToolAllowed(ItemStack stack) {
        return ListHandler.isToolAllowed(stack.getItem());
    }

    public static boolean isToolAllowed(Item item) {
        return !TOOL_DENY_LIST.contains(ForgeRegistries.ITEMS.getKey(item));
    }
}
