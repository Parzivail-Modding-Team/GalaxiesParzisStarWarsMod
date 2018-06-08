package com.parzivail.util.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class Tile2DofGimbal extends TileRotatable
{
	private float pitch = 0;

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 64537, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		super.onDataPacket(net, packet);
		this.readFromNBT(packet.getNbtCompound());
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setFloat("pitch", getPitch());
	}

	public float getPitch()
	{
		return pitch;
	}

	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		this.setPitch(tag.getFloat("pitch"));
	}
}
