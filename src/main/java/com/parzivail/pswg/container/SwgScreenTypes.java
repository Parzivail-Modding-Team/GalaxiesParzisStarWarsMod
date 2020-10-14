package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.screen.BlasterWorkbenchScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;

public class SwgScreenTypes
{
	public static class Workbench
	{
		public static final ScreenHandlerType<BlasterWorkbenchScreenHandler> Blaster = ScreenHandlerRegistry.registerSimple(Resources.identifier("workbench_blaster"), BlasterWorkbenchScreenHandler::new);
	}
}
