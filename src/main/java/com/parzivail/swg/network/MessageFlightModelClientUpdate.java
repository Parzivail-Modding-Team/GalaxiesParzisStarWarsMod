package com.parzivail.swg.network;

import com.parzivail.swg.ship.MultipartFlightModel;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageFlightModelClientUpdate extends PMessage<MessageFlightModelClientUpdate>
{
	public int shipId;
	public int shipDim;
	public String seatIds;

	public MessageFlightModelClientUpdate()
	{
	}

	public MessageFlightModelClientUpdate(MultipartFlightModel ship)
	{
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
		seatIds = ship.getSeatIds();
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		if (shipDim != Minecraft.getMinecraft().theWorld.provider.dimensionId)
			return null;

		Entity ship = Minecraft.getMinecraft().theWorld.getEntityByID(shipId);
		((MultipartFlightModel)ship).loadSeatsFromIds(seatIds);

		return null;
	}
}
