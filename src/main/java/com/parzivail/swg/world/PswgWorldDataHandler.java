package com.parzivail.swg.world;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.network.client.MessagePswgWorldDataSync;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class PswgWorldDataHandler extends WorldSavedData
{
	private static final String IDENTIFIER = Resources.MODID;

	public PswgWorldData data;

	public PswgWorldDataHandler()
	{
		this(IDENTIFIER);
	}

	public PswgWorldDataHandler(String id)
	{
		super(id);
		data = new PswgWorldData(this);
	}

	public static PswgWorldDataHandler get(World world)
	{
		PswgWorldDataHandler data = (PswgWorldDataHandler)world.loadItemData(PswgWorldDataHandler.class, IDENTIFIER);
		if (data == null)
		{
			data = new PswgWorldDataHandler();
			world.setItemData(IDENTIFIER, data);
		}
		return data;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		if (!nbt.hasKey(IDENTIFIER, Constants.NBT.TAG_COMPOUND))
			return;

		NBTTagCompound tag = nbt.getCompoundTag(IDENTIFIER);
		data.deserialize(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagCompound tag = new NBTTagCompound();
		data.serialize(tag);
		nbt.setTag(IDENTIFIER, tag);
	}

	void sync()
	{
		markDirty();
		StarWarsGalaxy.network.sendToAll(new MessagePswgWorldDataSync(this));
	}
}
