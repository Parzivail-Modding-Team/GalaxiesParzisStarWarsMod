package com.parzivail.swg.network;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.entity.ship.EntityShip;
import com.parzivail.swg.entity.ship.ShipInput;
import com.parzivail.swg.network.client.MessageShipClientInput;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import java.util.Set;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageShipInput extends PMessage<MessageShipInput>
{
	public int shipId;
	public int shipDim;
	public int input;

	public MessageShipInput()
	{
	}

	public MessageShipInput(EntityShip ship, ShipInput input)
	{
		shipId = ship.getEntityId();
		shipDim = ship.dimension;
		this.input = input.ordinal();
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

		ShipInput si = ShipInput.values()[input];
		ship.consumeInput(si);

		EntityTracker entitytracker = dim.getEntityTracker();
		Set<EntityPlayer> players = entitytracker.getTrackingPlayers(ship);
		for (EntityPlayer p : players)
		{
			StarWarsGalaxy.network.sendTo(new MessageShipClientInput(ship, si), (EntityPlayerMP)p);
		}
		return null;
	}
}
