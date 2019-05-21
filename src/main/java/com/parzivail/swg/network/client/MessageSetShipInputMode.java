package com.parzivail.swg.network.client;

import com.parzivail.swg.entity.EntityShip;
import com.parzivail.swg.network.PMessage;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSetShipInputMode extends PMessage<MessageSetShipInputMode>
{
	public int dimension;
	public int id;
	public int inputMode;

	public MessageSetShipInputMode()
	{
	}

	public MessageSetShipInputMode(EntityShip ship, int inputMode)
	{
		this.dimension = ship.dimension;
		this.id = ship.getEntityId();
		this.inputMode = inputMode;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.dimension);
		Entity entity = world.getEntityByID(this.id);

		if (!(entity instanceof EntityShip))
			return null;

		((EntityShip)entity).setInputMode(this.inputMode);

		return null;
	}
}
