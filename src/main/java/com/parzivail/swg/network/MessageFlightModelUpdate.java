package com.parzivail.swg.network;

import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageFlightModelUpdate extends PMessage<MessageFlightModelUpdate>
{
	public int shipId;
	public int shipDim;
	public Vector3f angularMomentum;
	public RotatedAxes orientation;
	public float throttle;

	public MessageFlightModelUpdate()
	{
	}

	public MessageFlightModelUpdate(BasicFlightModel ship)
	{
		angularMomentum = ship.angularMomentum;
		orientation = ship.orientation;
		throttle = ship.throttle;
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		Entity ship = MinecraftServer.getServer().worldServerForDimension(shipDim).getEntityByID(shipId);

		((BasicFlightModel)ship).angularMomentum = angularMomentum;
		((BasicFlightModel)ship).orientation = orientation;
		((BasicFlightModel)ship).throttle = throttle;

		return null;
	}
}
