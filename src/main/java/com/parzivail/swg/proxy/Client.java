package com.parzivail.swg.proxy;

import com.parzivail.swg.Resources;
import com.parzivail.util.common.Lumberjack;
import net.minecraft.client.Minecraft;

/**
 * Created by colby on 9/10/2017.
 */
public class Client extends Common
{
	@Override
	public void doSidedThings()
	{
		Resources.mc = Minecraft.getMinecraft();

		Lumberjack.log("Client proxy loaded!");
	}
}
