package dev.pswg.entity.effects;

import dev.pswg.Gadgets;
import dev.pswg.container.entity.GadgetsDamage;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class IntoxicatedEffect extends StatusEffect
{
	public IntoxicatedEffect()
	{
		super(StatusEffectCategory.HARMFUL, 100000);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier)
	{
		return true;
	}

	@Override
	public void onApplied(LivingEntity entity, int amplifier)
	{
		//addAttributeModifier(EntityAttributes.MOVEMENT_SPEED, Identifier.of("intoxicated"), -amplifier / 100f, EntityAttributeModifier.Operation.ADD_VALUE);
		super.onApplied(entity, amplifier);
	}

	@Override
	public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier)
	{
		return entity.damage(world, GadgetsDamage.create(world, GadgetsDamage.NERVE_GAS_DAMAGE_TYPE), (amplifier) / 8f);
	}
}
