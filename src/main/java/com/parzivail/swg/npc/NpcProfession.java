package com.parzivail.swg.npc;

public enum NpcProfession
{
	Merchant(0), Gunsmith(1), VehicleSalesman(2);

	private final int index;

	NpcProfession(int index)
	{
		this.index = index;
	}

	public static NpcProfession fromIndex(int index)
	{
		for (NpcProfession p : NpcProfession.values())
		{
			if (p.getIndex() == index)
				return p;
		}
		return null;
	}

	public int getIndex()
	{
		return index;
	}
}
