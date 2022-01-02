package de.melanx.excavar;

import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.client.ClientExcavar;
import de.melanx.excavar.network.DiggingNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

@Mod(Excavar.MODID)
public class Excavar {

    public static final String MODID = "excavar";
    private static DiggingNetwork network;
    private static PlayerHandler playerHandler;

    public Excavar() {
        network = new DiggingNetwork();
        playerHandler = new PlayerHandler();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.COMMON_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(new EventListener());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ClientExcavar();
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        network.registerPackets();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
    }

    public static DiggingNetwork getNetwork() {
        return network;
    }

    public static PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
