package com.parzivail.swg.network;

import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.swg.ship.Seat;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageSeatInit extends PMessage<MessageSeatInit>
{
	public Entity ship;
	public Entity seat;
	public Vector3f seatOffset;
	public int seatIdx;

	public MessageSeatInit()
	{

	}

	public MessageSeatInit(BasicFlightModel ship, Seat seat, int seatIdx)
	{
		this.ship = ship;
		this.seat = seat;
		this.seatOffset = seat.offset;
		this.seatIdx = seatIdx;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		if (this.seat == null || this.ship == null)
			return null;

		((BasicFlightModel)this.ship).seats[this.seatIdx] = (Seat)seat;
		((Seat)seat).attachToShip((BasicFlightModel)this.ship, this.seatOffset);

		return null;
	}
}
