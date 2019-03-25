package com.parzivail.util.binary.ned;

import java.util.UUID;

public class NedConnection
{
	public String text;
	public UUID connectId;
	public String[] payload;

	public NedConnection(String text, UUID connectId)
	{
		this.text = text;
		this.connectId = connectId;
	}
}
