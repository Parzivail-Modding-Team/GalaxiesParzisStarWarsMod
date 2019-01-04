package com.parzivail.util.network;

import com.parzivail.swg.network.MessageTransaction;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class TransactionBroker
{
	private final HashMap<String, Class<? extends Transaction>> transactions = new HashMap<>();
	private final SimpleNetworkWrapper network;

	public TransactionBroker(SimpleNetworkWrapper network)
	{
		this.network = network;
	}

	public NBTTagCompound create(Transaction t)
	{
		NBTTagCompound transaction = new NBTTagCompound();
		transaction.setString("opcode", t.getClass().getSimpleName());

		NBTTagCompound data = new NBTTagCompound();
		t.serialize(data);
		transaction.setTag("payload", data);
		return transaction;
	}

	public void register(Class<? extends Transaction> t)
	{
		transactions.put(t.getSimpleName(), t);
	}

	public Transaction transactionFor(String opcode)
	{
		Class<? extends Transaction> clazz = transactions.get(opcode);
		try
		{
			return clazz.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			// This should never happen
			e.printStackTrace();
			return null;
		}
	}

	public void dispatch(Transaction t)
	{
		network.sendToServer(new MessageTransaction(t));
	}

	public void dispatch(Transaction... t)
	{
		for (Transaction _t : t)
			dispatch(_t);
	}

	public void consume(NBTTagCompound transaction)
	{
		String opcode = transaction.getString("opcode");
		NBTTagCompound data = transaction.getCompoundTag("payload");
		Transaction t = transactionFor(opcode);
		if (t == null)
			return;

		t.deserialize(data);
		t.handle();
	}
}
