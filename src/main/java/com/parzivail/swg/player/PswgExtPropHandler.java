package com.parzivail.swg.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;

public class PswgExtPropHandler
{
	@SubscribeEvent
	public void entityConstruct(EntityConstructing e)
	{
		if (e.entity instanceof EntityPlayer && e.entity.getExtendedProperties(PswgExtProp.PROP_NAME) == null)
			e.entity.registerExtendedProperties(PswgExtProp.PROP_NAME, new PswgExtProp());
	}

	@SubscribeEvent
	public void entityJoinWorld(EntityJoinWorldEvent e)
	{
		if (!(e.entity instanceof EntityPlayer))
			return;

		PswgExtProp data = PswgExtProp.get(e.entity);
		if (data != null)
			data.sync();
	}

	@SubscribeEvent
	public void playerStartedTracking(StartTracking e)
	{
		if (!(e.entity instanceof EntityPlayer))
			return;

		PswgExtProp data = PswgExtProp.get(e.target);
		if (data != null)
			data.playerStartedTracking(e.entityPlayer);
	}

	@SubscribeEvent
	public void onClonePlayer(Clone e)
	{
		if (!(e.entity instanceof EntityPlayer))
			return;

		if (!e.wasDeath)
			return;

		NBTTagCompound compound = new NBTTagCompound();
		PswgExtProp.get(e.original).saveNBTData(compound);
		PswgExtProp.get(e.entityPlayer).loadNBTData(compound);
	}
}
