package de.melanx.excavar;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ConfigHandler {

    public static final ModConfigSpec SERVER_CONFIG;
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

    static {
        init(SERVER_BUILDER);
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static ModConfigSpec.IntValue blockLimit;
    public static ModConfigSpec.BooleanValue requiresCorrectTool;
    public static ModConfigSpec.BooleanValue disableDiagonals;
    public static ModConfigSpec.BooleanValue allowShapeSelection;
    public static ModConfigSpec.EnumValue<ShapeUtil.Type> allowedBlocks;
    public static ModConfigSpec.ConfigValue<List<? extends String>> deniedTools;

    public static void init(ModConfigSpec.Builder builder) {
        blockLimit = builder.comment("How many blocks should be mined at once?")
                .defineInRange("blocklimit", 16, 2, 8192);
        requiresCorrectTool = builder.comment("If set to true, you need the correct tool to break the blocks.")
                .define("requiresCorrectTool", true);
        disableDiagonals = builder.comment("If set to true, blocks will only be found on the 6 sides on each block.",
                        "It does not mean that only the 6 blocks around will be mined, but only these will be added to the list which should be mined.")
                .define("disableDiagonals", false);
        allowShapeSelection = builder.comment("If set to true, the player will be able to change shapes.")
                .define("allowShapeSelection", true);
        allowedBlocks = builder.comment("Defines which blocks can be mined with Excavar")
                .defineEnum("allowedBlocks", ShapeUtil.Type.ALL);
        deniedTools = builder.comment("A list of tools which aren't allowed.",
                        "You can use \"*\" to define a wildcard, e.g. \"minecraft:*_pickaxe\" will add all vanilla pickaxes to the list.")
                .defineList("forbiddenItems", Arrays.asList(
                        "botania:terra_axe",
                        "botania:terra_pick"
                ), s -> s instanceof String);
    }
}
