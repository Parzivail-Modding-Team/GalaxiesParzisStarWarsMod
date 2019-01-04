package com.parzivail.swg.transaction;

import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.util.item.NbtSave;
import com.parzivail.util.network.Transaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

public class TransactionUnlockAttachment extends Transaction<TransactionUnlockAttachment>
{
	@NbtSave
	public int playerDim;
	@NbtSave
	public int playerId;
	@NbtSave
	public int attachmentId;

	public TransactionUnlockAttachment()
	{

	}

	public TransactionUnlockAttachment(EntityPlayer player, BlasterAttachment attachment)
	{
		playerDim = player.dimension;
		playerId = player.getEntityId();
		attachmentId = attachment.getId();
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
