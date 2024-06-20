package de.melanx.excavar.client;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec CLIENT_CONFIG;
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    static {
        init(CLIENT_BUILDER);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static ModConfigSpec.BooleanValue onlyWhileSneaking;
    public static ModConfigSpec.BooleanValue preventToolsBreaking;
    public static ModConfigSpec.BooleanValue enableOutline;
    public static ModConfigSpec.BooleanValue considerDurability;

    public static void init(ModConfigSpec.Builder builder) {
        onlyWhileSneaking = builder.comment("If set to true, you need to sneak.")
                .define("sneakingRequired", false);
        preventToolsBreaking = builder.comment("Should tools being prevented from breaking? If true, mining will be stopped when durability is 1",
                        "If the durability is at 1 when breaking the first block, the tool will still break.")
                .define("preventBreaking", true);
        enableOutline = builder.comment("Should an outline be drawn when holding key to break multiple blocks?")
                .define("enableOutline", true);
        considerDurability = builder.comment("Should the tool's durability be considered when breaking blocks? If having enchantments like Unbreaking, this could show a wrong amount.")
                .define("considerDurability", false);
    }
}
