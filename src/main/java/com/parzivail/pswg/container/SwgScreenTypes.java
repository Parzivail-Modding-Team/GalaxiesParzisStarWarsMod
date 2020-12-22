package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.screen.*;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.NotNull;

public class SwgScreenTypes
{
	public static class Crate
	{
		public static final ScreenHandlerType<CrateOctagonScreenHandler> Octagon = ScreenHandlerRegistry.registerSimple(Resources.identifier("crate_octagon"), CrateOctagonScreenHandler::new);
		public static final ScreenHandlerType<CrateGenericSmallScreenHandler> MosEisley = ScreenHandlerRegistry.registerSimple(Resources.identifier("crate_mos_eisley"), makeScreenHandler(Crate.MosEisley));
		public static final ScreenHandlerType<CrateGenericSmallScreenHandler> ImperialCube = ScreenHandlerRegistry.registerSimple(Resources.identifier("crate_imperial_cube"), makeScreenHandler(Crate.ImperialCube));

		@NotNull
		private static ScreenHandlerRegistry.SimpleClientHandlerFactory<CrateGenericSmallScreenHandler> makeScreenHandler(ScreenHandlerType<?> handlerType)
		{
			return (sId, inv) -> new CrateGenericSmallScreenHandler(handlerType, sId, inv);
		}

		static void register()
		{
			// no-op to make sure the class is loaded
		}
	}

	public static class MoistureVaporator
	{
		public static final ScreenHandlerType<MoistureVaporatorScreenHandler> GX8 = ScreenHandlerRegistry.registerSimple(Resources.identifier("moisture_vaporator_gx8"), MoistureVaporatorScreenHandler::new);

		static void register()
		{
			// no-op to make sure the class is loaded
		}
	}

	public static class Workbench
	{
		public static final ScreenHandlerType<BlasterWorkbenchScreenHandler> Blaster = ScreenHandlerRegistry.registerSimple(Resources.identifier("blaster_workbench"), BlasterWorkbenchScreenHandler::new);
		public static final ScreenHandlerType<LightsaberForgeScreenHandler> Lightsaber = ScreenHandlerRegistry.registerSimple(Resources.identifier("lightsaber_forge"), LightsaberForgeScreenHandler::new);

		static void register()
		{
			// no-op to make sure the class is loaded
		}
	}

	public static void register()
	{
		Crate.register();
		MoistureVaporator.register();
		Workbench.register();
	}
}
