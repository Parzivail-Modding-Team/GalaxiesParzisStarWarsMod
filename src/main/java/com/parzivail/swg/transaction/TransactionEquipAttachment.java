package com.parzivail.swg.transaction;

import com.parzivail.swg.network.Transaction;
import com.parzivail.swg.tile.TileBlasterWorkbench;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachment;
import com.parzivail.swg.weapon.blastermodule.BlasterAttachments;
import com.parzivail.swg.weapon.blastermodule.BlasterData;
import com.parzivail.swg.weapon.blastermodule.barrel.BlasterBarrel;
import com.parzivail.swg.weapon.blastermodule.grip.BlasterGrip;
import com.parzivail.swg.weapon.blastermodule.scope.BlasterScope;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;

public class TransactionEquipAttachment extends Transaction<TransactionEquipAttachment>
{
	public int workbenchDim;
	public int workbenchX;
	public int workbenchY;
	public int workbenchZ;
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
		this.attachmentId = attachment.getId();
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
