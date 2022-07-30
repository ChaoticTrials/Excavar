package de.melanx.excavar.config;

import com.google.common.collect.Sets;
import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ListHandler {

    private static final Set<ResourceLocation> TOOL_DENY_LIST = Sets.newHashSet();

    private static void validate() {
        if (!TOOL_DENY_LIST.isEmpty()) {
            return;
        }

        Set<Pattern> deniedTools = ConfigHandler.deniedTools.get().stream().map(s -> Pattern.compile("^" + s.replace("*", ".*") + "$")).collect(Collectors.toSet());
        for (Pattern regex : deniedTools) {
            Set<ResourceLocation> itemIds = ForgeRegistries.ITEMS.getKeys();
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
            TOOL_DENY_LIST.clear();
        }
    }

    public static boolean isToolAllowed(ItemStack stack) {
        return ListHandler.isToolAllowed(stack.getItem());
    }

    public static boolean isToolAllowed(Item item) {
        ListHandler.validate();
        return !TOOL_DENY_LIST.contains(ForgeRegistries.ITEMS.getKey(item));
    }
}
