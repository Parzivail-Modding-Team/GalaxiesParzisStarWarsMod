package com.parzivail.pswg.entity.amphibian;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.passive.FrogEntity;

public class WorrtAttackablesSensor extends NearestVisibleLivingEntitySensor
{
	@Override
	protected boolean matches(LivingEntity entity, LivingEntity target)
	{
		if (entity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET))
		{
			return false;
		}
		return Sensor.testAttackableTargetPredicate(entity, target) && WorrtEntity.isValidWorrtFood(target) && target.isInRange(entity, 10.0);
	}

	@Override
	protected MemoryModuleType<LivingEntity> getOutputMemoryModule()
	{
		return MemoryModuleType.ATTACK_TARGET;
	}
}
