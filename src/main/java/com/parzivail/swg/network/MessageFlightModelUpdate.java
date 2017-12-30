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
		this.angularMomentum = ship.angularMomentum;
		this.orientation = ship.orientation;
		this.throttle = ship.throttle;
		this.shipId = ship.getEntityId();
		this.shipDim = ship.dimension;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		Entity ship = MinecraftServer.getServer().worldServerForDimension(this.shipDim).getEntityByID(this.shipId);

		((BasicFlightModel)ship).angularMomentum = this.angularMomentum;
		((BasicFlightModel)ship).orientation = this.orientation;
		((BasicFlightModel)ship).throttle = this.throttle;

		return null;
	}
}
