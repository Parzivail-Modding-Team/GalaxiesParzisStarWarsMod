package com.parzivail.swg.transaction;

import com.parzivail.swg.network.Transaction;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

public class TransactionUnlockAttachment extends Transaction<TransactionUnlockAttachment>
{
	public int playerDim;
	public int playerId;
	public int attachmentId;

	public TransactionUnlockAttachment()
	{

	}

	public TransactionUnlockAttachment(EntityPlayer player, BlasterAttachment attachment)
	{
		this.playerDim = player.dimension;
		this.playerId = player.getEntityId();
		this.attachmentId = attachment.getId();
	}

	@Override
	public void handle()
	{
		Entity player = DimensionManager.getWorld(playerDim).getEntityByID(playerId);
		if (!(player instanceof EntityPlayer))
			return;

		PswgExtProp p = PswgExtProp.get(player);
		if (p == null)
			return;

		p.unlockBlasterAttachment(attachmentId);
	}
}
