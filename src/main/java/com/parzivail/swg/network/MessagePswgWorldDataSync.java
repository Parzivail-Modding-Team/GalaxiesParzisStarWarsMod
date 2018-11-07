package com.parzivail.swg.network;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.world.PswgWorldDataHandler;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by colby on 12/29/2017.
 */
public class MessagePswgWorldDataSync extends PMessage<MessagePswgWorldDataSync>
{
	public NBTTagCompound worldData;

	public MessagePswgWorldDataSync()
	{

	}

	public MessagePswgWorldDataSync(PswgWorldDataHandler data)
	{
		worldData = new NBTTagCompound();
		data.writeToNBT(worldData);
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		StarWarsGalaxy.proxy.handleWorldDataSync(worldData);
		return null;
	}
}
