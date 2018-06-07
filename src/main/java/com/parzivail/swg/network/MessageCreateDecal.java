package com.parzivail.swg.network;

import com.parzivail.swg.render.decal.Decal;
import com.parzivail.swg.render.decal.WorldDecals;
import com.parzivail.util.network.PMessage;
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
	public float x;
	public float y;
	public float z;
	public float size;

	public MessageCreateDecal()
	{

	}

	public MessageCreateDecal(int dimension, int type, float x, float y, float z, float size, EnumFacing direction)
	{
		this.dimension = dimension;
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.size = size;
		this.direction = direction;
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldDecals.create(this.dimension, new Decal(type, x, y, z, size, direction));
		return null;
	}
}
