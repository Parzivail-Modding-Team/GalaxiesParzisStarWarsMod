package dev.pswg.item;

import dev.pswg.container.GadgetsSounds;
import net.minecraft.entity.LivingEntity;

public class FragmentationGrenadeSoundGroup extends ExplosionSoundGroup
{

	@Override
	public void playArmSound(LivingEntity player)
	{
		player.playSound(GadgetsSounds.ARM, 1f, 1f);
	}

	@Override
	public void playDisarmSound(LivingEntity player)
	{
		player.playSound(GadgetsSounds.DISARM, 1f, 1f);
	}

	@Override
	public void playThrowSound(LivingEntity player)
	{
		player.playSound(GadgetsSounds.THROW, 1f, 1f);
	}

	@Override
	public void playBeepingSound(LivingEntity player)
	{
		//SoundHelper.playFragmentationGrenadeItemSound(player);
	}
}
