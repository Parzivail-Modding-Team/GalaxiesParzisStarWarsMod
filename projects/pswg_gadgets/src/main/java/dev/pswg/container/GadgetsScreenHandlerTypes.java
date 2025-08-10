package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.feature.scrapping.ScrappingTableScreenHandler;
import dev.pswg.registry.Registrar;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmokerScreenHandler;

public class GadgetsScreenHandlerTypes
{

	public static final ScreenHandlerType<ScrappingTableScreenHandler> SCRAPPING_TABLE = Registrar.screenHandlerType(Gadgets.id("scrapping_table"), ScrappingTableScreenHandler::new);

	public static void register()
	{
	}
}
