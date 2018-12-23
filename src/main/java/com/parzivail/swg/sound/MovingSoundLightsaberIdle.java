package com.parzivail.swg.sound;

import com.parzivail.swg.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public class MovingSoundLightsaberIdle extends MovingSound
{
	private final EntityPlayer player;

	public MovingSoundLightsaberIdle(EntityPlayer player)
	{
		super(Resources.location("swg.fx.saber.idle"));
		this.player = player;
		attenuationType = ISound.AttenuationType.NONE;
		repeat = true;
		repeatDelay = 0;
	}

	public void update()
	{
		if (player.isDead)
			donePlaying = true;
	}
}
