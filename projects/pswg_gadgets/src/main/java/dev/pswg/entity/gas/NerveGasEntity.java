package dev.pswg.entity.gas;

import dev.pswg.container.GadgetsBlocks;
import dev.pswg.container.GadgetsParticleTypes;
import dev.pswg.container.entity.GadgetsEffects;
import dev.pswg.world.TickConstants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.util.math.random.Random;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NerveGasEntity extends Entity
{
	private final int DEFAULT_VOLUME = 400;
	/// how many blocks the gas can expand to times 10

	public ConcurrentMap<LivingEntity, Integer> toxicityIndex;
	public ConcurrentMap<BlockPos, Integer> blockConcentration;
	public int volume;

	public NerveGasEntity(EntityType<?> type, World world)
	{
		super(type, world);
		toxicityIndex = new ConcurrentHashMap<>(1024);
		blockConcentration = new ConcurrentHashMap<>(1024);
		volume = DEFAULT_VOLUME;
	}

	public void setVolume(int volume)
	{
		this.volume = volume;
	}

	public void setOriginalPos(BlockPos originalPos)
	{
		this.blockConcentration.put(originalPos, volume);
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

		var xList = nbt.getList("xList", 1);
		var yList = nbt.getList("yList", 1);
		var zList = nbt.getList("zList", 1);
		var conList = nbt.getList("concentrationList", 1);
		int s = xList.size();
		for (int i = 0; i < s; i++)
			blockConcentration.put(new BlockPos(xList.getInt(i), yList.getInt(i), zList.getInt(i)), conList.getInt(i));
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt)
	{

		List<Integer> xList = new ArrayList<>(List.of());
		List<Integer> yList = new ArrayList<>(List.of());
		List<Integer> zList = new ArrayList<>(List.of());

		blockConcentration.keySet().forEach(blockPos -> {
			xList.add(blockPos.getX());
			yList.add(blockPos.getY());
			zList.add(blockPos.getZ());
		});
		List<Integer> concentrationList = blockConcentration.values().stream().toList();
		nbt.putIntArray("xList", xList);
		nbt.putIntArray("yList", yList);
		nbt.putIntArray("zList", zList);
		nbt.putIntArray("concentrationList", concentrationList);
	}

	@Override
	public boolean hasNoGravity()
	{
		return true;
	}

	@Override
	public void onRemoved()
	{
		for (PlayerEntity player : getWorld().getPlayers())
			player.sendMessage(Text.of("blocks: " + blockConcentration.keySet().toString()), true);
		super.onRemoved();
	}

	public void debug()
	{
		if (getWorld() instanceof ServerWorld serverWorld)
		{
			AtomicInteger totalVolume = new AtomicInteger();
			AtomicInteger maxConcentration = new AtomicInteger(-1);
			AtomicInteger minConcentration = new AtomicInteger(volume);
			blockConcentration.forEach((pos, integer) -> {
				totalVolume.addAndGet(integer);
				if (maxConcentration.intValue() < integer)
					maxConcentration.set(integer);
				if (minConcentration.intValue() > integer)
					minConcentration.set(integer);
			});

			for (PlayerEntity player : serverWorld.getPlayers())
				player.sendMessage(Text.of("vol: " + totalVolume + "  count: " + (blockConcentration.size()) + " maxConc: " + maxConcentration + " avgConc: " + volume / Math.max(blockConcentration.size(), 1) + " minConc: " + minConcentration), true);
		}
	}

	@Override
	public void tick()
	{
		if (this.getWorld() instanceof ServerWorld serverWorld)
		{
			blockConcentration.keySet().forEach(pos -> {

				Direction.stream().forEach(direction -> {
					BlockPos offsetPos = pos.offset(direction);
					int originalConcentration = blockConcentration.get(pos);

					BlockState offsetState = getWorld().getBlockState(offsetPos);
					var view = getWorld().getChunkAsView(offsetPos.getX(), offsetPos.getZ());
					if (blockConcentration.containsKey(offsetPos))
					{
						int offsetConcentration = blockConcentration.get(offsetPos);
						if (originalConcentration - 1 >= blockConcentration.get(offsetPos))
						{
							blockConcentration.replace(pos, originalConcentration - Math.min((originalConcentration - offsetConcentration) / 2, 10));
							blockConcentration.replace(offsetPos, offsetConcentration + Math.min((originalConcentration - offsetConcentration) / 2, 10));
						}
					}
					else if ((offsetState.isIn(GadgetsBlocks.Tags.GASS_PASS_THROUGH) || (!offsetState.isSideSolidFullSquare(view, pos, direction.getOpposite()) && !offsetState.isSideSolidFullSquare(view, pos, direction))) && originalConcentration >= 12 && volume / 10 > blockConcentration.size())
					{
						blockConcentration.put(offsetPos, 10);
						blockConcentration.replace(pos, originalConcentration - 10);

						serverWorld.spawnParticles(GadgetsParticleTypes.NERVE_GAS_PARTICLE, offsetPos.getX(), offsetPos.getY(), offsetPos.getZ(), Random.create().nextBetween(1, 2), Random.create().nextBetween(-25, 25) / 100d, Random.create().nextBetween(-25, 25) / 100d, Random.create().nextBetween(-25, 25) / 100d, 0);
					}
				});
			});
		}



		if (age > 850)
		{
			toxicityIndex.clear();
			this.discard();
		}

		var entities = getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(32));
		for (LivingEntity entity : entities)
		{
			if (blockConcentration.containsKey(entity.getBlockPos()))
			{
				if (toxicityIndex.containsKey(entity))
					toxicityIndex.replace(entity, toxicityIndex.get(entity) + (blockConcentration.get(entity.getBlockPos())) / 10 + 1);
				else
					toxicityIndex.put(entity, 1);
			}
		}
		List<LivingEntity> decrement = new ArrayList<>(1024);
		toxicityIndex.forEach((livingEntity, integer) -> {
			if (!entities.contains(livingEntity))
				decrement.add(livingEntity);
		});
		for (LivingEntity entity : decrement)
		{
			toxicityIndex.replace(entity, (int)(toxicityIndex.get(entity) - 5f));
			if (toxicityIndex.get(entity) <= 1)
			{
				entity.removeStatusEffect(GadgetsEffects.INTOXICATED);
				toxicityIndex.remove(entity);
			}
		}
		List<LivingEntity> remove = new ArrayList<>(1024);
		toxicityIndex.forEach((livingEntity, toxicity) -> {
			int amplifier = toxicity / 50 - 1;
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
