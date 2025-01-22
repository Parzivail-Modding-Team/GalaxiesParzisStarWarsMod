package dev.pswg.entity;

import dev.pswg.Gadgets;
import dev.pswg.world.TickConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt)
	{
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
			this.discard();
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
				toxicityIndex.remove(entity);
		}
		if (getWorld() instanceof ServerWorld serverWorld)
			toxicityIndex.forEach((livingEntity, integer) -> {
				int amplifier = integer / 50 - 1;
				if (livingEntity.hasStatusEffect(Gadgets.INTOXICATED))
				{
					if (livingEntity.getStatusEffect(Gadgets.INTOXICATED).getAmplifier() != amplifier)
					{
						livingEntity.setStatusEffect(new StatusEffectInstance(Gadgets.INTOXICATED, TickConstants.ONE_DAY * 10, amplifier, false, false, true), this);
					}
				}
				else
					livingEntity.addStatusEffect(new StatusEffectInstance(Gadgets.INTOXICATED, TickConstants.ONE_DAY * 10, amplifier, false, false, true), this);
			});

		super.tick();
	}
}
