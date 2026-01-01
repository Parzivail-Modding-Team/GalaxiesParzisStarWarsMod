package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.feature.scrapping.table.ScrappingTableScreenHandler;
import dev.pswg.packet.MixerSyncS2CPayload;
import dev.pswg.registry.Registrar;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;

public class GadgetsScreenHandlerTypes
{

	public static final ExtendedScreenHandlerType<MixerScreenHandler, MixerSyncS2CPayload> MIXER = new ExtendedScreenHandlerType<>((syncId, inventory, data) -> new MixerScreenHandler(syncId, inventory, data.drinkEffects(), data.drinkColors(), data.drinkFoods()), MixerSyncS2CPayload.CODEC);
	public static final ScreenHandlerType<ScrappingTableScreenHandler> SCRAPPING_TABLE = Registrar.screenHandlerType(Gadgets.id("scrapping_table"), ScrappingTableScreenHandler::new);

	public static void register()
	{
		Registry.register(Registries.SCREEN_HANDLER, Gadgets.id("mixer"), MIXER);
	}
}
