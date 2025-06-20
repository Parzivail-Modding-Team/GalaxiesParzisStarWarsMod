package dev.pswg.entity.gas;

import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.entity.GadgetsEffects;
import dev.pswg.world.TickConstants;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NerveGasEntity extends GasEntity
{
	public ConcurrentMap<LivingEntity, Float> toxicityIndex;

	public NerveGasEntity(EntityType<?> type, World world)
	{
		super(type, world, 80, 900, 0.7f, GadgetsParticleTypes.NERVE_GAS_PARTICLE);
		toxicityIndex = new ConcurrentHashMap<>(1024);
	}

	@Override
	public void tick()
	{
		var entities = getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(32));
		for (LivingEntity entity : entities)
		{
			if (blockConcentration.containsKey(entity.getBlockPos()))
			{
				if (toxicityIndex.containsKey(entity))
					toxicityIndex.replace(entity, toxicityIndex.get(entity) + (blockConcentration.get(entity.getBlockPos())) / 10 + 1);
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
				entity.removeStatusEffect(GadgetsEffects.INTOXICATED);
				toxicityIndex.remove(entity);
			}
		}
		List<LivingEntity> remove = new ArrayList<>(1024);
		toxicityIndex.forEach((livingEntity, toxicity) -> {
			int amplifier = (int)(toxicity / 50 - 1);
			if (toxicity > 50)
				{
					if (livingEntity.hasStatusEffect(GadgetsEffects.INTOXICATED))
					{
						if (livingEntity.getStatusEffect(GadgetsEffects.INTOXICATED).getAmplifier() != amplifier)
							livingEntity.setStatusEffect(new StatusEffectInstance(GadgetsEffects.INTOXICATED, TickConstants.ONE_DAY * 100, amplifier, false, false, true), this);
					}
					else
						livingEntity.addStatusEffect(new StatusEffectInstance(GadgetsEffects.INTOXICATED, TickConstants.ONE_DAY * 100, amplifier, false, false, true), this);
				}
			if (livingEntity.isDead())
				remove.add(livingEntity);
		});
		remove.forEach(livingEntity -> {
			toxicityIndex.remove(livingEntity);
		});

		super.tick();
	}
}
