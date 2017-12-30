package com.parzivail.swg.network;

import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageFlightModelUpdate extends PMessage<MessageFlightModelUpdate>
{
	public Entity ship;
	public RotatedAxes orientation;
	public Vector3f angularMomentum;

	public MessageFlightModelUpdate()
	{

	}

	public MessageFlightModelUpdate(BasicFlightModel ship)
	{
		this.orientation = ship.orientation;
		this.angularMomentum = ship.angularMomentum;
		this.ship = ship;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		if (this.orientation == null || this.ship == null)
			return null;

		//((BasicFlightModel)this.ship).orientation = this.orientation;
		((BasicFlightModel)this.ship).angularMomentum = this.angularMomentum;

		return null;
	}
}
