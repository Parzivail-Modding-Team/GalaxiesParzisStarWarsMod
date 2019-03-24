package com.parzivail.swg.transaction;

import com.parzivail.swg.tile.TileNpcSpawner;
import com.parzivail.util.item.NbtSave;
import com.parzivail.util.network.Transaction;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class TransactionUpdateNpcSpawner extends Transaction<TransactionUpdateNpcSpawner>
{
	@NbtSave
	public int workbenchDim;
	@NbtSave
	public int workbenchX;
	@NbtSave
	public int workbenchY;
	@NbtSave
	public int workbenchZ;
	@NbtSave
	public String npcId;
	@NbtSave
	public boolean spawnImmediately;

	public TransactionUpdateNpcSpawner()
	{

	}

	public TransactionUpdateNpcSpawner(TileNpcSpawner workbench, String npcId, boolean spawnImmediately)
	{
		workbenchDim = workbench.getWorld().provider.dimensionId;
		workbenchX = workbench.xCoord;
		workbenchY = workbench.yCoord;
		workbenchZ = workbench.zCoord;
		this.npcId = npcId;
		this.spawnImmediately = spawnImmediately;
	}

	@Override
	public void handle()
	{
		TileEntity tile = DimensionManager.getWorld(workbenchDim).getTileEntity(workbenchX, workbenchY, workbenchZ);
		if (!(tile instanceof TileNpcSpawner))
			return;

		TileNpcSpawner spawner = (TileNpcSpawner)tile;
		spawner.setNpcId(npcId);
		spawner.setSpawnImmediately(spawnImmediately);
	}
}
