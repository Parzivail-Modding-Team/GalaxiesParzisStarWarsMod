package dev.pswg.item;

import dev.pswg.Gadgets;
import net.minecraft.entity.LivingEntity;

public abstract class ExplosionSoundGroup
{
	public abstract void playArmSound(LivingEntity player);

	public abstract void playDisarmSound(LivingEntity player);

	public abstract void playThrowSound(LivingEntity player);

	public abstract void playBeepingSound(LivingEntity player);
}

class ThermalDetonatorSoundGroup extends ExplosionSoundGroup
{

	@Override
	public void playArmSound(LivingEntity player)
	{
		player.playSound(Gadgets.ARM, 1f, 1f);
	}

	@Override
	public void playDisarmSound(LivingEntity player)
	{
		player.playSound(Gadgets.DISARM, 1f, 1f);
	}

	@Override
	public void playThrowSound(LivingEntity player)
	{
		player.playSound(Gadgets.THROW, 1f, 1f);
	}

	@Override
	public void playBeepingSound(LivingEntity player)
	{
		//SoundHelper.playDetonatorItemSound(player);
	}
}

class FragmentationGrenadeSoundGroup extends ExplosionSoundGroup
{

	@Override
	public void playArmSound(LivingEntity player)
	{
		player.playSound(Gadgets.ARM, 1f, 1f);
	}

	@Override
	public void playDisarmSound(LivingEntity player)
	{
		player.playSound(Gadgets.DISARM, 1f, 1f);
	}

	@Override
	public void playThrowSound(LivingEntity player)
	{
		player.playSound(Gadgets.THROW, 1f, 1f);
	}

	@Override
	public void playBeepingSound(LivingEntity player)
	{
		//SoundHelper.playFragmentationGrenadeItemSound(player);
	}
}
