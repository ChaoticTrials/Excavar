package de.melanx.excavar;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ConfigHandler {

    public static final ForgeConfigSpec SERVER_CONFIG;
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    static {
        init(SERVER_BUILDER);
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    public static ForgeConfigSpec.IntValue blockLimit;
    public static ForgeConfigSpec.IntValue xpUsage;
    public static ForgeConfigSpec.DoubleValue hungerUsage;
    public static ForgeConfigSpec.BooleanValue requiresCorrectTool;
    public static ForgeConfigSpec.BooleanValue disableDiagonals;
    public static ForgeConfigSpec.BooleanValue allowShapeSelection;
    public static ForgeConfigSpec.BooleanValue fistForbidden;
    public static ForgeConfigSpec.EnumValue<ShapeUtil.Type> allowedBlocks;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> deniedTools;

    public static void init(ForgeConfigSpec.Builder builder) {
        blockLimit = builder.comment("How many blocks should be mined at once?")
                .defineInRange("blocklimit", 16, 2, 8192);
        xpUsage = builder.comment("How much xp points will be used for one block")
                .defineInRange("xpUsage", 0, 0, 1000);
        hungerUsage = builder.comment("How much food exhaustion will caused by one block")
                .defineInRange("hungerUsage", 0.005, 0, 1000);
        requiresCorrectTool = builder.comment("If set to true, you need the correct tool to break the blocks.")
                .define("requiresCorrectTool", true);
        disableDiagonals = builder.comment("If set to true, blocks will only be found on the 6 sides on each block.",
                        "It does not mean that only the 6 blocks around will be mined, but only these will be added to the list which should be mined.")
                .define("disableDiagonals", false);
        allowShapeSelection = builder.comment("If set to true, the player will be able to change shapes.")
                .define("allowShapeSelection", true);
        fistForbidden = builder.comment("If user helds no item, it will not multi-mine")
                .define("fistForbidden", false);
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
