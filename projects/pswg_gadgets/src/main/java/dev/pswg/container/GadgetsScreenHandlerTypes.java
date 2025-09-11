package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.brewing.MixerScreenHandler;
import dev.pswg.feature.scrapping.table.ScrappingTableScreenHandler;
import dev.pswg.registry.Registrar;
import dev.pswg.screenHandler.CorrugatedCrateHandler;
import dev.pswg.screenHandler.CrateGenericSmallScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class GadgetsScreenHandlerTypes
{

	public static final ScreenHandlerType<MixerScreenHandler> MIXER = Registrar.screenHandlerType(Gadgets.id("mixer"), MixerScreenHandler::new);
	public static final ScreenHandlerType<ScrappingTableScreenHandler> SCRAPPING_TABLE = Registrar.screenHandlerType(Gadgets.id("scrapping_table"), ScrappingTableScreenHandler::new);
	public static final ScreenHandlerType<CrateGenericSmallScreenHandler> CORRUGATED = Registrar.screenHandlerType(Gadgets.id("corrugated_crate"), CorrugatedCrateHandler::new);

	public static void register()
	{
	}
}
