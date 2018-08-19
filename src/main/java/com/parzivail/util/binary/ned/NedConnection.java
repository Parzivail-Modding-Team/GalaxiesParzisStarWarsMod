package com.parzivail.util.binary.ned;

import java.util.UUID;

public class NedConnection
{
	public final String text;
	public final UUID connectedId;

	public NedConnection(String text, UUID connectedId)
	{
		this.text = text;
		this.connectedId = connectedId;
	}
}
