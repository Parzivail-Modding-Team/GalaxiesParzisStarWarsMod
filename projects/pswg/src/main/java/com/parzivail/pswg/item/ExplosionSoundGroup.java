package com.parzivail.pswg.item;

import com.parzivail.pswg.client.sound.SoundHelper;
import com.parzivail.pswg.container.SwgSounds;
import com.parzivail.pswg.entity.FragmentationGrenadeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ExplosionSoundGroup
{
	public abstract void playArmSound(PlayerEntity player);

	public abstract void playDisarmSound(PlayerEntity player);

	public abstract void playThrowSound(PlayerEntity player);

	public abstract void playBeepingSound(PlayerEntity player);
}

class ThermalDetonatorSoundGroup extends ExplosionSoundGroup
{

	@Override
	public void playArmSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_ARM, 1f, 1f);
	}

	@Override
	public void playDisarmSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_DISARM, 1f, 1f);
	}

	@Override
	public void playThrowSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_THROW, 1f, 1f);
	}

	@Override
	public void playBeepingSound(PlayerEntity player)
	{
		SoundHelper.playDetonatorItemSound(player);
	}
}

class FragmentationGrenadeSoundGroup extends ExplosionSoundGroup
{

	@Override
	public void playArmSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_ARM, 1f, 1f);
	}

	@Override
	public void playDisarmSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_DISARM, 1f, 1f);
	}

	@Override
	public void playThrowSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_THROW, 1f, 1f);
	}

	@Override
	public void playBeepingSound(PlayerEntity player)
	{
		SoundHelper.playFragmentationGrenadeItemSound(player);
	}
}

class SonicImploderSoundGroup extends ExplosionSoundGroup
{

	@Override
	public void playArmSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_ARM, 1f, 1f);
	}

	@Override
	public void playDisarmSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_DISARM, 1f, 1f);
	}

	@Override
	public void playThrowSound(PlayerEntity player)
	{
		player.playSound(SwgSounds.Explosives.THERMAL_DETONATOR_THROW, 1f, 1f);
	}

	@Override
	public void playBeepingSound(PlayerEntity player)
	{
		SoundHelper.playDetonatorItemSound(player);
	}
}
