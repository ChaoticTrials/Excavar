package de.melanx.excavar;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {

    public static final ForgeConfigSpec COMMON_CONFIG;
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

    static {
        init(COMMON_BUILDER);
        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static ForgeConfigSpec.IntValue blockLimit;
    public static ForgeConfigSpec.BooleanValue requiresCorrectTool;
    public static ForgeConfigSpec.BooleanValue disableDiagonals;
    public static ForgeConfigSpec.EnumValue<ShapeUtil.Type> allowedBlocks;

    public static void init(ForgeConfigSpec.Builder builder) {
        blockLimit = builder.comment("How many blocks should be mined at once?")
                .defineInRange("blocklimit", 16, 2, 8192);
        requiresCorrectTool = builder.comment("If set to true, you need the correct tool to break the blocks.")
                .define("requiresCorrectTool", true);
        disableDiagonals = builder.comment("If set to true, blocks will only be found on the 6 sides on each block.",
                        "It does not mean that only the 6 blocks around will be mined, but only these will be added to the list which should be mined.")
                .define("disableDiagonals", false);
        allowedBlocks = builder.comment("Defines which blocks can be mined with Excavar")
                .defineEnum("allowedBlocks", ShapeUtil.Type.ALL);
    }
}
