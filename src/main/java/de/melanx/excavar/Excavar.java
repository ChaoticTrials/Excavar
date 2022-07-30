package de.melanx.excavar;

import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.api.shape.Shapes;
import de.melanx.excavar.client.ClientExcavar;
import de.melanx.excavar.config.ListHandler;
import de.melanx.excavar.impl.shape.EasyShapeless;
import de.melanx.excavar.impl.shape.PlantsShapeless;
import de.melanx.excavar.impl.shape.Shapeless;
import de.melanx.excavar.impl.shape.Tunnel;
import de.melanx.excavar.network.DiggingNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Excavar.MODID)
public class Excavar {

    public static final String MODID = "excavar";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static DiggingNetwork network;
    private static PlayerHandler playerHandler;

    public Excavar() {
        network = new DiggingNetwork();
        playerHandler = new PlayerHandler();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHandler.SERVER_CONFIG);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ListHandler::onConfigChange);
        MinecraftForge.EVENT_BUS.register(new EventListener());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            new ClientExcavar();
        }
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        network.registerPackets();
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));

        Shapes.register(Shapes.SHAPELESS, new Shapeless());
        Shapes.register(Shapes.EASY_SHAPELESS, new EasyShapeless(), false);
        Shapes.register(Shapes.PLANTS_SHAPELESS, new PlantsShapeless(), false);
        Shapes.register(Shapes.TUNNEL, new Tunnel());
    }

    public static DiggingNetwork getNetwork() {
        return network;
    }

    public static PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
