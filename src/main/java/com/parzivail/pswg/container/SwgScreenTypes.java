package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import com.parzivail.pswg.screen.MosEisleyCrateScreenHandler;
import com.parzivail.pswg.screen.OctagonCrateScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class SwgScreenTypes
{
	public static class Workbench
	{
		public static final ScreenHandlerType<BlasterWorkbenchScreenHandler> Blaster = ScreenHandlerRegistry.registerSimple(Resources.identifier("workbench_blaster"), BlasterWorkbenchScreenHandler::new);
	}

	public static class Crate
	{
		public static final ScreenHandlerType<OctagonCrateScreenHandler> Octagon = ScreenHandlerRegistry.registerSimple(Resources.identifier("crate_octagon"), OctagonCrateScreenHandler::new);
		public static final ScreenHandlerType<MosEisleyCrateScreenHandler> MosEisley = ScreenHandlerRegistry.registerSimple(Resources.identifier("crate_mos_eisley"), MosEisleyCrateScreenHandler::new);
	}
}
