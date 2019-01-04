package com.parzivail.util.block;

import com.parzivail.util.sound.AtmoSound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

public class TileAtmoSound extends TileEntity
{
	@SideOnly(Side.CLIENT)
	private AtmoSound sound;

	@Override
	public void updateEntity()
	{
		//		if (worldObj.isRemote)
		//		{
		//			updateEntityClient();
		//		}
	}

	@Override
	public void invalidate()
	{
		//		super.invalidate();
		//		if (worldObj.isRemote)
		//		{
		//			updateSound();
		//		}
	}

	@SideOnly(Side.CLIENT)
	private void updateEntityClient()
	{
		//		updateSound();
	}

	@SideOnly(Side.CLIENT)
	private void updateSound()
	{
		//		ResourceLocation soundRL = getSound();
		//		if (!isInvalid() && soundRL != null)
		//		{
		//			Sfx sh = FMLClientHandler.instance().getClient().getSoundHandler();
		//			if (sound == null || !sh.isSoundPlaying(sound))
		//				sh.play(sound = new AtmoSound(soundRL, xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, 1, 1));
		//		}
		//		else if (sound != null)
		//		{
		//			sound.endPlaying();
		//			sound = null;
		//		}
	}

	@SideOnly(Side.CLIENT)
	public void stopSound()
	{
		//		ResourceLocation soundRL = getSound();
		//		if (soundRL != null)
		//			FMLClientHandler.instance().getClient().getSoundHandler().stopSound(sound);
	}

	public ResourceLocation getSound()
	{
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return super.getRenderBoundingBox().expand(20, 20, 20);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}
}
