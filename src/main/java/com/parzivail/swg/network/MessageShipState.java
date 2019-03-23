package com.parzivail.swg.network;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.swg.network.client.MessageShipClientState;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.util.Set;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageShipState extends PMessage<MessageShipState>
{
	public int shipId;
	public int shipDim;
	public Vector3f position;
	public NBTTagCompound state;

	public MessageShipState()
	{
	}

	public MessageShipState(EntityShip ship)
	{
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
		position = new Vector3f((float)ship.posX, (float)ship.posY, (float)ship.posZ);

		state = new NBTTagCompound();
		ship.writeTransientState(state);
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldServer dim = MinecraftServer.getServer().worldServerForDimension(shipDim);
		if (dim == null)
			return null;

		EntityShip ship = (EntityShip)dim.getEntityByID(shipId);
		if (ship == null)
			return null;

		ship.setPosition(position.x, position.y, position.z);
		ship.readTransientState(state);

		Entity driver = ship.seats[0].riddenByEntity;

		EntityTracker entitytracker = dim.getEntityTracker();
		Set<EntityPlayer> players = entitytracker.getTrackingPlayers(ship);
		for (EntityPlayer p : players)
		{
			if (p == driver)
				continue;
			StarWarsGalaxy.network.sendTo(new MessageShipClientState(ship), (EntityPlayerMP)p);
		}

		return null;
	}
}
