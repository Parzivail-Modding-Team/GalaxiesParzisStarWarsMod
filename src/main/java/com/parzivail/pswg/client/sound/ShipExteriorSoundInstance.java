package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.entity.ship.ShipEntity;
import com.parzivail.util.math.Ease;
import com.parzivail.util.math.MathUtil;
import com.parzivail.util.math.MovingAverage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShipExteriorSoundInstance extends MovingSoundInstance
{
	private final ShipEntity source;

	private final MovingAverage _floatingPitch = new MovingAverage(10, 0.75f);
	private final MovingAverage _floatingVolume = new MovingAverage(10, 0);

	public ShipExteriorSoundInstance(ShipEntity source, SoundEvent sound)
	{
		super(sound, SoundCategory.PLAYERS);
		this.source = source;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0f;
	}

	public boolean canPlay()
	{
		return !this.source.isSilent();
	}

	public boolean shouldAlwaysPlay()
	{
		return true;
	}

	public void tick()
	{
		if (this.source.isRemoved())
		{
			this.setDone();
			return;
		}

		var throttle = source.getThrottle();

		_floatingVolume.update(MathHelper.clamp(throttle, 0, 1));
		_floatingPitch.update(MathHelper.clamp(throttle, 0, 2) / 6f);

		this.volume = Ease.inCubic(_floatingVolume.getAverage());

		var mc = MinecraftClient.getInstance();
		if (mc.getCameraEntity() != null)
			this.pitch = 0.75f + _floatingPitch.getAverage() + MathUtil.calculateDopplerShift(source, mc.getCameraEntity());
		else
			this.pitch = 0.75f + _floatingPitch.getAverage();

		this.x = (float)this.source.getX();
		this.y = (float)this.source.getY();
		this.z = (float)this.source.getZ();
	}
}
