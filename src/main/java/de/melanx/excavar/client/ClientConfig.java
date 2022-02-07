package de.melanx.excavar.client;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static final ForgeConfigSpec CLIENT_CONFIG;
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    static {
        init(CLIENT_BUILDER);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static ForgeConfigSpec.BooleanValue onlyWhileSneaking;
    public static ForgeConfigSpec.BooleanValue preventToolsBreaking;
    public static ForgeConfigSpec.BooleanValue enableOutline;

    public static void init(ForgeConfigSpec.Builder builder) {
        onlyWhileSneaking = builder.comment("If set to true, you need to sneak.")
                .define("sneakingRequired", false);
        preventToolsBreaking = builder.comment("Should tools being prevented from breaking? If true, mining will be stopped when durability is 1",
                        "If the durability is at 1 when breaking the first block, the tool will still break.")
                .define("preventBreaking", true);
        enableOutline = builder.comment("Should an outline be drawn when holding key to break multiple blocks?")
                .define("enableOutline", true);
    }
}
