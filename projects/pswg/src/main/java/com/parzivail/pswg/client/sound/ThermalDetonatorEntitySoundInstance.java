package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class ThermalDetonatorEntitySoundInstance extends MovingSoundInstance implements ISoftRepeatSound
{
	private final ThermalDetonatorEntity detonatorEntity;

	public ThermalDetonatorEntitySoundInstance(ThermalDetonatorEntity detonatorEntity)
	{
		super(SwgSounds.Explosives.THERMAL_DETONATOR_BEEP, SoundCategory.PLAYERS, SoundInstance.createRandom());
		this.detonatorEntity = detonatorEntity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 1F;
		this.x = (float)detonatorEntity.getX();
		this.y = (float)detonatorEntity.getY();
		this.z = (float)detonatorEntity.getZ();
	}

	public ThermalDetonatorEntity getDetonator()
	{
		return detonatorEntity;
	}

	@Override
	public void tick()
	{
		if (detonatorEntity.isRemoved() || !areConditionsMet(detonatorEntity))
		{
			setDone();
			return;
		}
		x = (float)this.detonatorEntity.getX();
		y = (float)this.detonatorEntity.getY();
		z = (float)this.detonatorEntity.getZ();

		var player = MinecraftClient.getInstance().player;
		var d = player.distanceTo(detonatorEntity);
		if (d < 32)
		{
			volume = ((32 - d) / 32);
		}
		else
		{
			volume = 0;
		}
	}

	public static boolean areConditionsMet(ThermalDetonatorEntity tde)
	{
		return tde.isPrimed();
	}
}
