package com.parzivail.swg.network.client;

import com.parzivail.swg.network.PMessage;
import com.parzivail.util.item.ILeftClickInterceptor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by colby on 12/29/2017.
 */
public class MessageItemLeftClick extends PMessage<MessageItemLeftClick>
{
	public int playerDim;
	public int playerId;

	public MessageItemLeftClick()
	{

	}

	public MessageItemLeftClick(EntityPlayer thePlayer)
	{
		playerDim = thePlayer.dimension;
		playerId = thePlayer.getEntityId();
	}

	@Override
	public IMessage handleMessage(MessageContext context)
	{
		WorldServer world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(this.playerDim);
		Entity ePlayer = world.getEntityByID(playerId);
		if (!(ePlayer instanceof EntityPlayer))
			return null;

		EntityPlayer player = (EntityPlayer)ePlayer;

		ItemStack heldItem = player.getHeldItemMainhand();
		if (!(heldItem.getItem() instanceof ILeftClickInterceptor))
			return null;

		((ILeftClickInterceptor)heldItem.getItem()).onItemLeftClick(heldItem, player.world, player);

		return null;
	}
}
