package com.parzivail.swg.ship;

import org.lwjgl.util.vector.Vector3f;

public class SeatData
{
	public final String name;
	public final SeatRole role;
	public final Vector3f pos;

	public SeatData(String name, SeatRole role, Vector3f pos)
	{
		this.name = name;
		this.role = role;
		this.pos = pos;
	}
}
