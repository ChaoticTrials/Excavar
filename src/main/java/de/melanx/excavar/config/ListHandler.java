package de.melanx.excavar.config;

import de.melanx.excavar.ConfigHandler;
import de.melanx.excavar.Excavar;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

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
        if (event.getConfig().getModId().equals(Excavar.MODID)) {
            FORBIDDEN_TOOLS = null;
        }
    }

    public static boolean isToolAllowed(ItemStack stack) {
        return ListHandler.isToolAllowed(stack.getItem());
    }

    public static boolean isToolAllowed(Item item) {
        ListHandler.validate();
        return !FORBIDDEN_TOOLS.matcher(String.valueOf(ForgeRegistries.ITEMS.getKey(item))).matches();
    }
}
