package com.parzivail.swg.network;

import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.swg.proxy.Client;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageShipClientOrientation extends PMessage<MessageShipClientOrientation>
{
	public int shipId;
	public int shipDim;
	public RotatedAxes orientation;
	public Vector3f position;
	public Vector3f velocity;

	public MessageShipClientOrientation()
	{
	}

	public MessageShipClientOrientation(EntityShip ship)
	{
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
		orientation = ship.orientation;
		position = new Vector3f((float)ship.posX, (float)ship.posY, (float)ship.posZ);
		velocity = new Vector3f((float)ship.motionX, (float)ship.motionY, (float)ship.motionZ);
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		EntityShip ship = (EntityShip)Client.mc.theWorld.getEntityByID(shipId);
		ship.orientation = orientation.clone();

		ship.setPosition(position.x, position.y, position.z);

		ship.motionX = velocity.x;
		ship.motionY = velocity.y;
		ship.motionZ = velocity.z;

		return null;
	}
}
