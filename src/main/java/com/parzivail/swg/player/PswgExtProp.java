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
import org.apache.commons.lang3.ArrayUtils;

public class PswgExtProp extends NbtSerializable<PswgExtProp> implements IExtendedEntityProperties
{
	public static transient final String PROP_NAME = Resources.MODID + "_eep";

	private transient Entity entity;
	private transient World world;

	@Deprecated
	public int creditBalance;

	@Deprecated
	public int[] unlockedBlasterAttachments;

	public static void register()
	{
		MinecraftForge.EVENT_BUS.register(new PswgExtPropHandler());
	}

	@Override
	public void init(Entity entity, World world)
	{
		this.entity = entity;
		this.world = world;
	}

	public static PswgExtProp get(Entity p)
	{
		if (p == null)
			return null;
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
		if (!compound.hasKey(PROP_NAME, Constants.NBT.TAG_COMPOUND))
			return;

		NBTTagCompound data = compound.getCompoundTag(PROP_NAME);
		deserialize(data);
	}

	public void setCreditBalance(int creditBalance)
	{
		this.creditBalance = creditBalance;
		sync();
	}

	public void addCreditBalance(int delta)
	{
		this.creditBalance += delta;
		sync();
	}

	public int getCreditBalance()
	{
		return creditBalance;
	}

	public void unlockBlasterAttachment(int attachmentId)
	{
		unlockedBlasterAttachments = ArrayUtils.add(unlockedBlasterAttachments, attachmentId);
		sync();
	}

	public boolean isBlasterAttachmentUnlocked(int attachmentId)
	{
		return ArrayUtils.contains(unlockedBlasterAttachments, attachmentId);
	}

	void sync()
	{
		if (world.isRemote)
			return;

		EntityTracker tracker = ((WorldServer)world).getEntityTracker();
		MessagePswgExtPropSync message = new MessagePswgExtPropSync((EntityPlayer)entity, this);

		StarWarsGalaxy.network.sendTo(message, (EntityPlayerMP)entity);
		for (EntityPlayer entityPlayer : tracker.getTrackingPlayers(entity))
			StarWarsGalaxy.network.sendTo(message, (EntityPlayerMP)entityPlayer);

	}

	void playerStartedTracking(EntityPlayer entityPlayer)
	{
		StarWarsGalaxy.network.sendTo(new MessagePswgExtPropSync((EntityPlayer)entity, this), (EntityPlayerMP)entityPlayer);
	}
}
