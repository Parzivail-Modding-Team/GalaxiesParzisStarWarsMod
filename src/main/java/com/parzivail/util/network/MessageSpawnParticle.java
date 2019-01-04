package com.parzivail.util.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.client.Minecraft;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageSpawnParticle extends PMessage<MessageSpawnParticle>
{
	public int dimension;
	public String name;
	public double x;
	public double y;
	public double z;
	public double vx;
	public double vy;
	public double vz;

	public MessageSpawnParticle()
	{

	}

	public MessageSpawnParticle(int dimension, String name, double x, double y, double z, double vx, double vy, double vz)
	{
		this.dimension = dimension;
		this.name = name;
		this.x = x;
		this.y = y;
		this.z = z;
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null)
			return null;
		mc.theWorld.spawnParticle(name, x, y, z, vx, vy, vz);
		return null;
	}
}
