package com.parzivail.swg.tile.atmosphere;

import com.parzivail.util.sound.AtmoSound;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileAtmoSound extends TileEntity
{
	@SideOnly(Side.CLIENT)
	private AtmoSound sound;

	@Override
	public void updateEntity()
	{
		if (worldObj.isRemote)
		{
			updateEntityClient();
		}
	}

	private void updateEntityClient()
	{
		final ResourceLocation soundRL = getSound();
		if (!isInvalid() && soundRL != null)
		{
			if (sound == null)
				FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound = new AtmoSound(soundRL, this.xCoord + 0.5f, this.yCoord + 0.5f, this.zCoord + 0.5f, 1, 1));
		}
		else if (sound != null)
		{
			sound.endPlaying();
			sound = null;
		}
	}

	public ResourceLocation getSound()
	{
		return null;
	}
}
