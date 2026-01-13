package dev.pswg.container;

import dev.pswg.blockEntity.screenHandler.CorrugatedCrateHandler;
import dev.pswg.blockEntity.screenHandler.CrateGenericSmallScreenHandler;
import dev.pswg.Galaxies;
import dev.pswg.registry.Registrar;
import net.minecraft.screen.ScreenHandlerType;

public class GalaxiesScreenHandlerTypes
{
	public static final ScreenHandlerType<CrateGenericSmallScreenHandler> CORRUGATED = Registrar.screenHandlerType(Galaxies.id("corrugated_crate"), CorrugatedCrateHandler::new);
	public static void register(){}
}
