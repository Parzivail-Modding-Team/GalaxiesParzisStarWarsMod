package com.parzivail.swg.proxy;

import com.parzivail.swg.register.EntityRegister;
import com.parzivail.swg.register.StructureRegister;
import com.parzivail.swg.register.WorldRegister;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class SwgProxy
{
	public void preInit(FMLPreInitializationEvent e)
	{
		WorldRegister.register();
		StructureRegister.register();
		EntityRegister.register();
	}

	public void init(FMLInitializationEvent e)
	{
	}

	public void registerItemRenderer(Item itemBlock, String name)
	{
	}
}
