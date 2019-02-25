package com.parzivail.swg.network;

import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageShipOrientation extends PMessage<MessageShipOrientation>
{
	public int shipId;
	public int shipDim;
	public RotatedAxes orientation;

	public MessageShipOrientation()
	{
	}

	public MessageShipOrientation(EntityShip ship)
	{
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
		orientation = ship.orientation;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldServer dim = MinecraftServer.getServer().worldServerForDimension(shipDim);
		if (dim == null)
			return null;

		Entity ship = dim.getEntityByID(shipId);
		((EntityShip)ship).orientation = orientation.clone();

		return null;
	}
}
