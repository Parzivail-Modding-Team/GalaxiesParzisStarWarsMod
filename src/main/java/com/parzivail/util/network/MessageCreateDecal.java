package com.parzivail.util.network;

import com.parzivail.util.render.decal.Decal;
import com.parzivail.util.render.decal.WorldDecals;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.util.EnumFacing;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageCreateDecal extends PMessage<MessageCreateDecal>
{
	public int dimension;
	public int type;
	public EnumFacing direction;
	public int blockX;
	public int blockY;
	public int blockZ;
	public float x;
	public float y;
	public float z;
	public float size;

	public MessageCreateDecal()
	{

	}

	public MessageCreateDecal(int dimension, int type, int blockX, int blockY, int blockZ, float x, float y, float z, float size, EnumFacing direction)
	{
		this.dimension = dimension;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockZ = blockZ;
		this.size = size;
		this.direction = direction;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldDecals.create(dimension, new Decal(type, blockX, blockY, blockZ, x, y, z, size, direction));
		return null;
	}
}
