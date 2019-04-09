package com.parzivail.swg.handler;

import com.parzivail.swg.register.BlockRegister;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SwgEventHandler
{
	@SubscribeEvent
	public void on(RegistryEvent.Register<Block> event)
	{
		BlockRegister.register(event);
	}
}
