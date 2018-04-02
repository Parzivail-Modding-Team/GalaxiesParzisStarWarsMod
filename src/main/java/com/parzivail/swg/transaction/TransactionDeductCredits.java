package com.parzivail.swg.transaction;

import com.parzivail.swg.network.Transaction;
import com.parzivail.swg.player.PswgExtProp;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;

public class TransactionDeductCredits extends Transaction<TransactionDeductCredits>
{
	public int playerDim;
	public int playerId;
	public int price;

	public TransactionDeductCredits()
	{

	}

	public TransactionDeductCredits(EntityPlayer player, int price)
	{
		this.playerDim = player.dimension;
		this.playerId = player.getEntityId();
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
