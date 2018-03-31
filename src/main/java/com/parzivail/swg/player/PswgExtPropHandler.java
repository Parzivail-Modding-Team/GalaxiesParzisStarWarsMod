package com.parzivail.swg.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PswgExtPropHandler
{
	@SubscribeEvent
	public void entityConstruct(EntityEvent.EntityConstructing e)
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
			data.dataChanged();
	}

	@SubscribeEvent
	public void playerStartedTracking(PlayerEvent.StartTracking e)
	{
		if (!(e.entity instanceof EntityPlayer))
			return;

		PswgExtProp data = PswgExtProp.get(e.target);
		if (data != null)
			data.playerStartedTracking(e.entityPlayer);
	}

	@SubscribeEvent
	public void onClonePlayer(PlayerEvent.Clone e)
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
