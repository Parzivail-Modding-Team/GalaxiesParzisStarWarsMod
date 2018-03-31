package com.parzivail.swg.player;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.network.MessagePswgExtPropSync;
import com.parzivail.util.item.NbtSerializable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

public class PswgExtProp extends NbtSerializable<PswgExtProp> implements IExtendedEntityProperties
{
	public static transient final String PROP_NAME = Resources.MODID + "_eep";

	private transient Entity entity;
	private transient World world;

	public int creditBalance;

	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(new PswgExtPropHandler());
	}

	public static PswgExtProp get(Entity p)
	{
		return (PswgExtProp)p.getExtendedProperties(PROP_NAME);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound data = new NBTTagCompound();

		serialize(data);

		compound.setTag(PROP_NAME, data);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		if (compound.hasKey(PROP_NAME, Constants.NBT.TAG_COMPOUND))
		{
			NBTTagCompound data = compound.getCompoundTag(PROP_NAME);

			deserialize(data);
		}
	}

	@Override
	public void init(Entity entity, World world)
	{
		this.entity = entity;
		this.world = world;
	}

	void dataChanged()
	{
		if (world.isRemote)
			return;

		EntityTracker tracker = ((WorldServer)world).getEntityTracker();
		MessagePswgExtPropSync message = new MessagePswgExtPropSync((EntityPlayer)entity, this);

		for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(entity))
			StarWarsGalaxy.network.sendTo(message, (EntityPlayerMP)entityPlayer);

	}

	void playerStartedTracking(EntityPlayer entityPlayer)
	{
		StarWarsGalaxy.network.sendTo(new MessagePswgExtPropSync((EntityPlayer)entity, this), (EntityPlayerMP)entityPlayer);
	}
}
