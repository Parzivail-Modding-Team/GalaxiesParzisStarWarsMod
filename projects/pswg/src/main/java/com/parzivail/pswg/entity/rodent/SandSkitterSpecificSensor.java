package com.parzivail.pswg.entity.rodent;

import com.google.common.collect.ImmutableSet;
import com.parzivail.pswg.entity.amphibian.WorrtEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;
import java.util.Set;

public class SandSkitterSpecificSensor extends Sensor<LivingEntity>
{
	@Override
	protected void sense(ServerWorld world, LivingEntity entity)
	{
		Optional<LivingEntity> optional5 = Optional.empty();
		Brain<?> brain = entity.getBrain();
		LivingTargetCache targetCache = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(LivingTargetCache.empty());
		for (LivingEntity livingEntity : targetCache.iterate(livingEntity -> true)) {
			if (optional5.isEmpty()) {
				if (livingEntity instanceof PlayerEntity || livingEntity instanceof MobEntity) {
					if (livingEntity instanceof WorrtEntity) continue;
					optional5 = Optional.of(livingEntity);
				}
			}
		}
		brain.remember(MemoryModuleType.AVOID_TARGET, optional5);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules()
	{
		return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS);
	}
}
