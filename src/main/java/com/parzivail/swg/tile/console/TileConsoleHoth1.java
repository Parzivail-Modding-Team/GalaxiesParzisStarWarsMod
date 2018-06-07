package com.parzivail.swg.tile.console;

import com.parzivail.swg.Resources;
import com.parzivail.util.math.MathUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileConsoleHoth1 extends TileEntity
{
	int facing = 0;

	public int color1 = 0;
	public int color2 = 0;
	public int color3 = 0;
	public int color4 = 0;
	public int color5 = 0;
	public int color6 = 0;

	@Override
	public void updateEntity()
	{
		if (MathUtil.oneIn(20))
			color1 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(50))
			color2 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(70))
			color3 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(10))
			color4 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(100))
			color5 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
		if (MathUtil.oneIn(120))
			color6 = MathUtil.getRandomElement(Resources.PANEL_LIGHT_COLORS);
	}

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
	public void writeToNBT(NBTTagCompound p_145841_1_)
	{
		super.writeToNBT(p_145841_1_);
		p_145841_1_.setInteger("facing", getFacing());
	}

	public int getFacing()
	{
		return facing;
	}

	public void setFacing(int facing)
	{
		this.facing = facing;
	}

	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_)
	{
		super.readFromNBT(p_145839_1_);
		this.setFacing(p_145839_1_.getInteger("facing"));
	}
}
