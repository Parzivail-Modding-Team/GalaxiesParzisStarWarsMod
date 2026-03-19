package dev.pswg.entity.gas;

import dev.pswg.Gadgets;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.entity.GadgetsEffects;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class NerveGasEntity extends GasEntity
{
	public ConcurrentMap<LivingEntity, Float> toxicityIndex;

	public NerveGasEntity(EntityType<?> type, Level world)
	{
		super(type, world, 80, 900, 0.7f, 0.7f, GadgetsParticleTypes.NERVE_GAS_PARTICLE);
		toxicityIndex = new ConcurrentHashMap<>(1024);
	}

	@Override
	public void tick()
	{
		var entities = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32));
		for (LivingEntity entity : entities)
		{
			if (massMap.containsKey(entity.blockPosition()) && massMap.get(entity.blockPosition()) > 0.25f)
			{
				if (toxicityIndex.containsKey(entity))
					toxicityIndex.replace(entity, toxicityIndex.get(entity) + (massMap.get(entity.blockPosition())) / 10 + 1);
				else
					toxicityIndex.put(entity, 1f);
			}
		}
		List<LivingEntity> decrement = new ArrayList<>(1024);
		toxicityIndex.forEach((livingEntity, integer) -> {
			if (!entities.contains(livingEntity))
				decrement.add(livingEntity);
		});
		for (LivingEntity entity : decrement)
		{
			toxicityIndex.replace(entity, (toxicityIndex.get(entity) - 5f));
			if (toxicityIndex.get(entity) <= 1)
			{
				entity.removeEffect(GadgetsEffects.INTOXICATED);
				toxicityIndex.remove(entity);
			}
		}
		List<LivingEntity> remove = new ArrayList<>(1024);
		toxicityIndex.forEach((livingEntity, toxicity) -> {
			int amplifier = (int)(toxicity / 50 - 1);
			if (toxicity > 50)
				{
					if (livingEntity.hasEffect(GadgetsEffects.INTOXICATED))
					{
						if (livingEntity.getEffect(GadgetsEffects.INTOXICATED).getAmplifier() != amplifier)
						{
							livingEntity.forceAddEffect(new MobEffectInstance(GadgetsEffects.INTOXICATED, 100, amplifier, false, false, true), this);
						}
					}
					else
						livingEntity.addEffect(new MobEffectInstance(GadgetsEffects.INTOXICATED, 100, amplifier, false, false, true), this);
				}
			if (livingEntity.isDeadOrDying())
				remove.add(livingEntity);
		});
		remove.forEach(livingEntity -> {
			toxicityIndex.remove(livingEntity);
		});

		super.tick();
	}
}
