package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.screen.*;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class SwgScreenTypes
{
	public static class Crate
	{
		public static final ScreenHandlerType<CrateOctagonScreenHandler> Octagon = ScreenHandlerRegistry.registerSimple(Resources.identifier("crate_octagon"), CrateOctagonScreenHandler::new);
		public static final ScreenHandlerType<CrateMosEisleyScreenHandler> MosEisley = ScreenHandlerRegistry.registerSimple(Resources.identifier("crate_mos_eisley"), CrateMosEisleyScreenHandler::new);
		public static final ScreenHandlerType<CrateImperialCubeScreenHandler> ImperialCube = ScreenHandlerRegistry.registerSimple(Resources.identifier("crate_imperial_cube"), CrateImperialCubeScreenHandler::new);

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
		public static final ScreenHandlerType<BlasterWorkbenchScreenHandler> Blaster = ScreenHandlerRegistry.registerSimple(Resources.identifier("workbench_blaster"), BlasterWorkbenchScreenHandler::new);

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
