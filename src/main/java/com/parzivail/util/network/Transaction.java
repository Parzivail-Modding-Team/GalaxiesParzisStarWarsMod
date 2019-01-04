package com.parzivail.util.network;

import com.parzivail.util.item.NbtSerializable;

public abstract class Transaction<T extends Transaction> extends NbtSerializable<T>
{
	public abstract void handle();
}
