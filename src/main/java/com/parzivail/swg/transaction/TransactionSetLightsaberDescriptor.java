package com.parzivail.swg.transaction;

import com.parzivail.swg.item.lightsaber.LightsaberData;
import com.parzivail.swg.item.lightsaber.LightsaberDescriptor;
import com.parzivail.swg.network.Transaction;
import com.parzivail.swg.tile.TileLightsaberForge;
import com.parzivail.util.item.NbtSave;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class TransactionSetLightsaberDescriptor extends Transaction<TransactionSetLightsaberDescriptor>
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
	public LightsaberDescriptor descriptor;

	public TransactionSetLightsaberDescriptor()
	{

	}

	public TransactionSetLightsaberDescriptor(TileLightsaberForge workbench, LightsaberDescriptor descriptor)
	{
		workbenchDim = workbench.getWorld().provider.dimensionId;
		workbenchX = workbench.xCoord;
		workbenchY = workbench.yCoord;
		workbenchZ = workbench.zCoord;
		this.descriptor = descriptor;
	}

	@Override
	public void handle()
	{
		TileEntity tile = DimensionManager.getWorld(workbenchDim).getTileEntity(workbenchX, workbenchY, workbenchZ);
		if (!(tile instanceof TileLightsaberForge))
			return;

		TileLightsaberForge workbench = (TileLightsaberForge)tile;
		ItemStack stack = workbench.getLightsaber();
		if (stack == null)
			return;

		LightsaberData data = new LightsaberData(stack);
		data.descriptor = descriptor;
		data.serialize(stack.stackTagCompound);
	}
}
