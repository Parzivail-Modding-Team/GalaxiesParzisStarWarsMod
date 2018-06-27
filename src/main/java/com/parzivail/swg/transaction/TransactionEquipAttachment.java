package com.parzivail.swg.transaction;

import com.parzivail.swg.item.blaster.data.BlasterAttachment;
import com.parzivail.swg.item.blaster.data.BlasterAttachments;
import com.parzivail.swg.item.blaster.data.BlasterData;
import com.parzivail.swg.item.blaster.data.barrel.BlasterBarrel;
import com.parzivail.swg.item.blaster.data.grip.BlasterGrip;
import com.parzivail.swg.item.blaster.data.scope.BlasterScope;
import com.parzivail.swg.network.Transaction;
import com.parzivail.swg.tile.TileBlasterWorkbench;
import com.parzivail.util.item.NbtSave;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class TransactionEquipAttachment extends Transaction<TransactionEquipAttachment>
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
	public int attachmentId;

	public TransactionEquipAttachment()
	{

	}

	public TransactionEquipAttachment(TileBlasterWorkbench workbench, BlasterAttachment attachment)
	{
		workbenchDim = workbench.getWorld().provider.dimensionId;
		workbenchX = workbench.xCoord;
		workbenchY = workbench.yCoord;
		workbenchZ = workbench.zCoord;
		attachmentId = attachment.getId();
	}

	@Override
	public void handle()
	{
		TileEntity tile = DimensionManager.getWorld(workbenchDim).getTileEntity(workbenchX, workbenchY, workbenchZ);
		if (!(tile instanceof TileBlasterWorkbench))
			return;

		TileBlasterWorkbench workbench = (TileBlasterWorkbench)tile;
		ItemStack stack = workbench.getBlaster();
		if (stack == null)
			return;

		BlasterData data = new BlasterData(stack);
		BlasterAttachment attachment = BlasterAttachments.ATTACHMENTS.get(attachmentId);
		switch (attachment.type)
		{
			case SCOPE:
				data.setScope((BlasterScope)attachment);
				break;
			case BARREL:
				data.setBarrel((BlasterBarrel)attachment);
				break;
			case GRIP:
				data.setGrip((BlasterGrip)attachment);
				break;
		}
		data.serialize(stack.stackTagCompound);
	}
}
