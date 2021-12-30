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

    public static void init(ForgeConfigSpec.Builder builder) {
        onlyWhileSneaking = builder.comment("If set to true, you need to sneak.")
                .define("sneakingRequired", false);
    }
}
