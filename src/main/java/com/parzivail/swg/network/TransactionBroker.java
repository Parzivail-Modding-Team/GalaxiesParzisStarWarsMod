package com.parzivail.swg.network;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.transaction.TransactionDeductCredits;
import com.parzivail.swg.transaction.TransactionEquipAttachment;
import com.parzivail.swg.transaction.TransactionUnlockAttachment;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;

public class TransactionBroker
{
	private static final HashMap<String, Class<? extends Transaction>> transactions = new HashMap<>();

	static
	{
		register(TransactionDeductCredits.class);
		register(TransactionUnlockAttachment.class);
		register(TransactionEquipAttachment.class);
	}

	private static void register(Class<? extends Transaction> t)
	{
		transactions.put(t.getSimpleName(), t);
	}

	public static Transaction transactionFor(String opcode)
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

	public static void dispatch(Transaction t)
	{
		StarWarsGalaxy.network.sendToServer(new MessageTransaction(t));
	}

	public static void dispatch(Transaction... t)
	{
		for (Transaction _t : t)
			dispatch(_t);
	}

	public static void consume(String opcode, NBTTagCompound data)
	{
		Transaction t = transactionFor(opcode);
		if (t == null)
			return;

		t.deserialize(data);
		t.handle();
	}
}
