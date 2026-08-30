package de.melanx.excavar.data;

import de.melanx.excavar.Excavar;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Excavar.MODID)
public class DataCreator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(true, new ModItemTags(generator.getPackOutput(), event.getLookupProvider()));
    }
}
