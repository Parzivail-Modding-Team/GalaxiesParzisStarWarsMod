package com.parzivail.swg.network;

import com.parzivail.swg.ship.BasicFlightModel;
import com.parzivail.swg.ship.Seat;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageSeatInit extends PMessage<MessageSeatInit>
{
	public int shipId;
	public int seatId;
	public Vector3f seatOffset;
	public int seatIdx;

	public MessageSeatInit()
	{

	}

	public MessageSeatInit(BasicFlightModel ship, Seat seat, int seatIdx)
	{
		this.shipId = ship.getEntityId();
		this.seatId = seat.getEntityId();
		this.seatOffset = seat.offset;
		this.seatIdx = seatIdx;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		Entity ship = Minecraft.getMinecraft().theWorld.getEntityByID(this.shipId);
		Entity seat = Minecraft.getMinecraft().theWorld.getEntityByID(this.seatId);

		//((BasicFlightModel)ship).seats[this.seatIdx] = (Seat)seat;
		((Seat)seat).attachToShip((BasicFlightModel)ship, this.seatOffset, this.seatIdx);

		return null;
	}
}
