package com.parzivail.swg.transaction;

import com.parzivail.swg.player.PswgExtProp;
import com.parzivail.util.item.NbtSave;
import com.parzivail.util.network.Transaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

public class TransactionDeductCredits extends Transaction<TransactionDeductCredits>
{
	@NbtSave
	public int playerDim;
	@NbtSave
	public int playerId;
	@NbtSave
	public int price;

	public TransactionDeductCredits()
	{

	}

	public TransactionDeductCredits(EntityPlayer player, int price)
	{
		playerDim = player.dimension;
		playerId = player.getEntityId();
		this.price = price;
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

		p.addCreditBalance(-price);
	}
}
