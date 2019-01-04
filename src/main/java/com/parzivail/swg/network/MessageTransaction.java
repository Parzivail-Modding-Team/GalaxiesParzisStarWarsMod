package com.parzivail.swg.network;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.network.PMessage;
import com.parzivail.util.network.Transaction;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageTransaction extends PMessage<MessageTransaction>
{
	public NBTTagCompound transaction;

	public MessageTransaction()
	{
	}

	public MessageTransaction(Transaction t)
	{
		transaction = StarWarsGalaxy.transactionBroker.create(t);
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		StarWarsGalaxy.transactionBroker.consume(transaction);
		return null;
	}
}
