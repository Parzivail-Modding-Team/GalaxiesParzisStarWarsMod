package dev.pswg.item;

import dev.pswg.container.GadgetsSounds;
import net.minecraft.world.entity.LivingEntity;

public abstract class ExplosionSoundGroup
{
	public abstract void playArmSound(LivingEntity player);

	public abstract void playDisarmSound(LivingEntity player);

	public abstract void playThrowSound(LivingEntity player);

	public abstract void playBeepingSound(LivingEntity player);
}

