package com.parzivail.swg.network;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.util.math.RotatedAxes;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.util.Set;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageShipOrientation extends PMessage<MessageShipOrientation>
{
	public int shipId;
	public int shipDim;
	public RotatedAxes orientation;
	public Vector3f position;
	public float throttle;

	public MessageShipOrientation()
	{
	}

	public MessageShipOrientation(EntityShip ship)
	{
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
		orientation = ship.orientation;
		position = new Vector3f((float)ship.posX, (float)ship.posY, (float)ship.posZ);
		throttle = ship.throttle;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldServer dim = MinecraftServer.getServer().worldServerForDimension(shipDim);
		if (dim == null)
			return null;

		EntityShip ship = (EntityShip)dim.getEntityByID(shipId);
		ship.orientation = orientation.clone();

		ship.setPosition(position.x, position.y, position.z);
		ship.throttle = throttle;

		Entity driver = ship.seats[0].riddenByEntity;

		EntityTracker entitytracker = dim.getEntityTracker();
		Set<EntityPlayer> players = entitytracker.getTrackingPlayers(ship);
		for (EntityPlayer p : players)
		{
			if (p == driver)
				continue;
			StarWarsGalaxy.network.sendTo(new MessageShipClientOrientation(ship), (EntityPlayerMP)p);
		}

		return null;
	}
}
