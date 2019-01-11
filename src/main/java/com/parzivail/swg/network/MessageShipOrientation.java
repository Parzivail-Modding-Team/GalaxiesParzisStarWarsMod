package com.parzivail.swg.network;

import com.parzivail.swg.entity.EntityShipParentTest;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;

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

	public MessageShipOrientation(EntityShipParentTest ship)
	{
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
		orientation = ship.orientation;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		if (shipDim != Client.mc.theWorld.provider.dimensionId)
			return null;

		Entity ship = Client.mc.theWorld.getEntityByID(shipId);
		((EntityShipParentTest)ship).orientation = orientation.clone();

		return null;
	}
}
