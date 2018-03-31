package com.parzivail.swg.network;

import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.util.network.PMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;

/**
 * Created by colby on 12/29/2017.
 */
public class MessagePswgExtPropSync extends PMessage<MessagePswgExtPropSync>
{
	public int dimension;
	public int entityId;
	public NBTTagCompound ieep;

	public MessagePswgExtPropSync()
	{

	}

	public MessagePswgExtPropSync(EntityPlayer player, PswgExtProp props)
	{
		dimension = player.dimension;
		entityId = player.getEntityId();
		ieep = new NBTTagCompound();
		props.saveNBTData(ieep);
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		DimensionManager.getWorld(dimension).getEntityByID(entityId).getExtendedProperties(PswgExtProp.PROP_NAME).loadNBTData(ieep);
		return null;
	}
}
