package dev.pswg.entity.effects;

import dev.pswg.Gadgets;
import dev.pswg.container.entity.GadgetsDamage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class IntoxicatedEffect extends MobEffect
{
	public IntoxicatedEffect()
	{
		super(MobEffectCategory.HARMFUL, 100000);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier)
	{
		return true;
	}

	@Override
	public void onEffectStarted(LivingEntity entity, int amplifier)
	{
		//addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, Identifier.of("intoxicated"), -amplifier / 100f, EntityAttributeModifier.Operation.ADD_VALUE);
		super.onEffectStarted(entity, amplifier);
	}

	@Override
	public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier)
	{
		return entity.hurtServer(world, GadgetsDamage.create(world, GadgetsDamage.NERVE_GAS_DAMAGE_TYPE), (amplifier) / 8f);
	}
}
