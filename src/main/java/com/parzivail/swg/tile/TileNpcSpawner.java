package com.parzivail.swg.tile;

import com.parzivail.util.common.Lumberjack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileNpcSpawner extends TileEntity
{
	private String npcId = "";
	private boolean spawnImmediately;

	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		npcId = tag.getString("npcId");
		spawnImmediately = tag.getBoolean("spawnImmediately");
	}

	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		tag.setString("npcId", npcId);
		tag.setBoolean("spawnImmediately", spawnImmediately);
	}

	@Override
	public void updateEntity()
	{
		if (spawnImmediately)
		{
			if (worldObj.isRemote)
				return;

			setSpawnImmediately(false);

			Lumberjack.debug("Spawning %s", npcId);
			Entity e = getNpcById(npcId);
			e.setPosition(xCoord + 0.5, yCoord, zCoord + 0.5);
			worldObj.spawnEntityInWorld(e);
			worldObj.breakBlock(xCoord, yCoord, zCoord, false);
		}
	}

	private Entity getNpcById(String npcId)
	{
		return new EntityPig(worldObj);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		writeToNBT(nbttagcompound);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -1, nbttagcompound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
	{
		super.onDataPacket(net, packet);
		readFromNBT(packet.getNbtCompound());
	}

	public void setNpcId(String npcId)
	{
		this.npcId = npcId;
		sync();
	}

	public String getNpcId()
	{
		return npcId;
	}

	public void setSpawnImmediately(boolean spawnImmediately)
	{
		this.spawnImmediately = spawnImmediately;
		sync();
	}

	private void sync()
	{
		markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public boolean shouldSpawnImmediately()
	{
		return spawnImmediately;
	}
}
