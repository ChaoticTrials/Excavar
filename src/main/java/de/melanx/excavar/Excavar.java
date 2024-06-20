package de.melanx.excavar;

import de.melanx.excavar.api.PlayerHandler;
import de.melanx.excavar.api.shape.Shapes;
import de.melanx.excavar.config.ListHandler;
import de.melanx.excavar.impl.shape.EasyShapeless;
import de.melanx.excavar.impl.shape.PlantsShapeless;
import de.melanx.excavar.impl.shape.Shapeless;
import de.melanx.excavar.impl.shape.Tunnel;
import de.melanx.excavar.network.DiggingNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = Excavar.MODID)
public class Excavar {

    public static final String MODID = "excavar";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    private static PlayerHandler playerHandler;

    public Excavar(IEventBus bus, ModContainer modContainer) {
        playerHandler = new PlayerHandler();
        modContainer.registerConfig(ModConfig.Type.SERVER, ConfigHandler.SERVER_CONFIG);
        bus.addListener(this::commonSetup);
        bus.addListener(DiggingNetwork::onRegisterPayloadHandler);
        bus.addListener(ListHandler::onConfigChange);
        NeoForge.EVENT_BUS.register(new EventListener());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        Shapes.register(Shapes.SHAPELESS, new Shapeless());
        Shapes.register(Shapes.EASY_SHAPELESS, new EasyShapeless(), false);
        Shapes.register(Shapes.PLANTS_SHAPELESS, new PlantsShapeless(), false);
        Shapes.register(Shapes.TUNNEL, new Tunnel());
    }

    public static PlayerHandler getPlayerHandler() {
        return playerHandler;
    }
}
