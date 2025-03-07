package dev.pswg.entity;

import dev.pswg.Gadgets;
import dev.pswg.container.entity.GadgetsEffects;
import dev.pswg.world.TickConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.*;

public class NerveGasEntity extends Entity
{
	public Map<LivingEntity, Integer> toxicityIndex;

	public NerveGasEntity(EntityType<?> type, World world)
	{
		super(type, world);
		toxicityIndex = HashMap.newHashMap(1024);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount)
	{
		return false;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt)
	{
		var entityIdList = nbt.getList("entityList", 1);
		var entityToxicityList = nbt.getList("toxicity", 1);
		var s = entityIdList.size();
		for (int i = 0; i < s; i++)
			toxicityIndex.put((LivingEntity)getWorld().getEntityById(entityIdList.getInt(i)), entityToxicityList.getInt(i));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt)
	{
		List<Integer> list = new ArrayList<>(List.of());
		List<Integer> finalList1 = list;
		toxicityIndex.forEach((livingEntity, integer) -> finalList1.add(livingEntity.getId()));
		nbt.putIntArray("entityList", finalList1);
		list = new ArrayList<>(List.of());
		List<Integer> finalList = list;
		toxicityIndex.forEach((livingEntity, integer) -> finalList.add(integer));
		nbt.putIntArray("toxicity", finalList);
	}

	@Override
	public boolean hasNoGravity()
	{
		return true;
	}

	@Override
	public void tick()
	{
		if (age > 850)
		{
			toxicityIndex.clear();
			this.discard();
		}
		var entities = getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox());
		for (LivingEntity entity : entities)
		{
			if (toxicityIndex.containsKey(entity))
				toxicityIndex.replace(entity, toxicityIndex.get(entity) + 1);
			else
				toxicityIndex.put(entity, 5);
		}
		List<LivingEntity> decrement = new ArrayList<>(1024);
		toxicityIndex.forEach((livingEntity, integer) -> {
			if (!entities.contains(livingEntity))
				decrement.add(livingEntity);
		});
		for (LivingEntity entity : decrement)
		{
			toxicityIndex.replace(entity, (int)(toxicityIndex.get(entity) - 3f));
			if (toxicityIndex.get(entity) <= 3)
			{
				entity.removeStatusEffect(GadgetsEffects.INTOXICATED);
				toxicityIndex.remove(entity);
			}
		}
		//if (getWorld() instanceof ServerWorld serverWorld)
		toxicityIndex.forEach((livingEntity, toxicity) -> {
			int amplifier = toxicity / 50 - 1;
			if (toxicity > 50)
				{
					if (livingEntity.hasStatusEffect(GadgetsEffects.INTOXICATED))
					{
						if (livingEntity.getStatusEffect(GadgetsEffects.INTOXICATED).getAmplifier() != amplifier)
						{
							livingEntity.setStatusEffect(new StatusEffectInstance(GadgetsEffects.INTOXICATED, TickConstants.ONE_DAY * 100, amplifier, false, false, true), this);
						}
					}
					else
						livingEntity.addStatusEffect(new StatusEffectInstance(GadgetsEffects.INTOXICATED, TickConstants.ONE_DAY * 100, amplifier, false, false, true), this);
				}
			if (livingEntity.isDead())
			{
				toxicityIndex.remove(livingEntity);
			}
			});

		super.tick();
	}
}
