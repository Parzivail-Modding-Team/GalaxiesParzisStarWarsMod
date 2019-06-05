package com.parzivail.swg.proxy;

import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.register.EntityRegister;
import com.parzivail.swg.register.StructureRegister;
import com.parzivail.swg.register.WorldRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.MovementInput;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Common
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

	public void postInit(FMLPostInitializationEvent e)
	{
	}

	public void registerItemRenderer(Item itemBlock, String name)
	{
	}

	public void onRegisterItem(RegistryEvent.Register<Item> event)
	{
	}

	public MovementInput getMovementInput(EntityPlayer player)
	{
		return null;
	}

	public void captureShipInput(EntityPlayer pilot, EntityShip entityShip)
	{
	}

	public void notifyPlayer(ITextComponent message, boolean actionBar)
	{
	}
}
