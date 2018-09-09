package com.parzivail.swg.world;

import com.parzivail.util.item.NbtSave;
import com.parzivail.util.item.NbtSerializable;

public class PswgWorldData extends NbtSerializable<PswgWorldData>
{
	private final PswgWorldDataHandler data;

	@NbtSave
	protected boolean isImperialMeltdown;

	public PswgWorldData(PswgWorldDataHandler pswgWorldDataHandler)
	{
		data = pswgWorldDataHandler;
	}

	public boolean getIsImperialMeltdown()
	{
		return isImperialMeltdown;
	}

	public void setIsImperialMeltdown(boolean isImperialMeltdown)
	{
		this.isImperialMeltdown = isImperialMeltdown;
		data.sync();
	}
}
