package com.parzivail.pswg.client.sound;

import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.FragmentationGrenadeEntity;
import com.parzivail.pswg.entity.ThermalDetonatorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

@Environment(EnvType.CLIENT)
public class FragmentationGrenadeEntitySoundInstance extends MovingSoundInstance implements ISoftRepeatSound
{
	private final FragmentationGrenadeEntity fragmentationEntity;

	public FragmentationGrenadeEntitySoundInstance(FragmentationGrenadeEntity fragmentationEntity)
	{
		super(SwgSounds.Explosives.FRAGMENTATION_GRENADE_BEEP, SoundCategory.PLAYERS, SoundInstance.createRandom());
		this.fragmentationEntity = fragmentationEntity;
		this.repeat = true;
		this.repeatDelay = 0;
		this.volume = 0.25F;
		this.x = (float)fragmentationEntity.getX();
		this.y = (float)fragmentationEntity.getY();
		this.z = (float)fragmentationEntity.getZ();
	}

	public FragmentationGrenadeEntity getGrenade()
	{
		return fragmentationEntity;
	}

	@Override
	public void tick()
	{
		if (fragmentationEntity.isRemoved() || !areConditionsMet(fragmentationEntity))
		{
			setDone();
			return;
		}
		x = (float)this.fragmentationEntity.getX();
		y = (float)this.fragmentationEntity.getY();
		z = (float)this.fragmentationEntity.getZ();

		var player = MinecraftClient.getInstance().player;
		if (player != null)
		{
			float d = player.distanceTo(fragmentationEntity);
			if (d < 16f)
			{
				volume = ((16f - d) / 16f);
			}
			else
			{
				volume = 0;
			}
		}
	}

	public static boolean areConditionsMet(FragmentationGrenadeEntity fge)
	{
		return fge.isPrimed();
	}
}
