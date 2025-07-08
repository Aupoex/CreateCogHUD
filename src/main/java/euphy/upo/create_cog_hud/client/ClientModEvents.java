package euphy.upo.create_cog_hud.client;

import euphy.upo.create_cog_hud.CreateCogHUD;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber(modid = CreateCogHUD.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {

    private ClientModEvents() {}

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {

        NeoForge.EVENT_BUS.register(new HealthBarRenderer());

        NeoForge.EVENT_BUS.register(new ArmorBarRenderer());

        NeoForge.EVENT_BUS.register(new FoodBarRenderer());

    }
}

