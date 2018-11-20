package com.parzivail.util.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileRotatable extends TileAtmoSound
{
	private float facing;
	private boolean didConvertRotationFormat;

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 64537, tag);
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (!didConvertRotationFormat && !worldObj.isRemote)
		{
			int l = (int)(facing * 90 / 45f);
			if (l < 0)
				l += 8;
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, l, 3);
			didConvertRotationFormat = true;
		}
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setFloat("facing", getFacing());
	}

	public float getFacing()
	{
		return facing;
	}

	public void setFacing(float facing)
	{
		this.facing = facing;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		setFacing(tag.getFloat("facing"));
	}
}
