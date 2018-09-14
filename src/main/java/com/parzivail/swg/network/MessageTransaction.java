package com.parzivail.swg.network;

import com.parzivail.util.network.PMessage;
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
		transaction = new NBTTagCompound();
		transaction.setString("opcode", t.getClass().getSimpleName());

		NBTTagCompound data = new NBTTagCompound();
		t.serialize(data);
		transaction.setTag("payload", data);
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		String opcode = transaction.getString("opcode");
		NBTTagCompound data = transaction.getCompoundTag("payload");
		TransactionBroker.consume(opcode, data);
		return null;
	}
}
