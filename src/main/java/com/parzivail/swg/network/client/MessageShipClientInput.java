package com.parzivail.swg.network.client;

import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.swg.entity.ship.ShipInput;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageShipClientInput extends PMessage<MessageShipClientInput>
{
	public int shipId;
	public int shipDim;
	public int input;

	public MessageShipClientInput()
	{
	}

	public MessageShipClientInput(EntityShip ship, ShipInput input)
	{
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
		this.input = input.ordinal();
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		EntityShip ship = (EntityShip)Client.mc.theWorld.getEntityByID(shipId);

		ship.consumeInput(ShipInput.values()[input]);

		return null;
	}
}
