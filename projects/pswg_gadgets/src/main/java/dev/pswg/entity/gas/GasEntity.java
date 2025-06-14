package dev.pswg.entity.gas;

import dev.pswg.container.GadgetsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GasEntity extends Entity
{
	private final int DEFAULT_VOLUME;
	private final int MAX_AGE;
	private final int DENSITY;
	private final SimpleParticleType particleType;

	public ConcurrentMap<LivingEntity, Integer> toxicityIndex;
	public ConcurrentMap<BlockPos, Integer> blockConcentration;
	public int volume;

	public GasEntity(EntityType<?> type, World world, int defaultVolume, int maxAge, int density, SimpleParticleType particle)
	{
		super(type, world);
		DEFAULT_VOLUME = defaultVolume;
		MAX_AGE = maxAge;
		DENSITY = density;
		particleType = particle;
		volume = DEFAULT_VOLUME;
		toxicityIndex = new ConcurrentHashMap<>(1024);
		blockConcentration = new ConcurrentHashMap<>(1024);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder)
	{

	}

	public void setVolume(int volume)
	{
		this.volume = volume;
	}

	public void addDefaultPos(BlockPos originalPos)
	{
		this.blockConcentration.put(originalPos, volume);
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
		var world = getWorld();
		float pressure = 1 + (1 - ((float)(blockConcentration.size()) / (volume / 1000)));
		boolean foundPos = false;
		if (this.age == 1)
		{
			var state = world.getBlockState(getBlockPos().up());
			if (state.isIn(GadgetsBlocks.Tags.GAS_PASS_THROUGH) || !state.isSolid())
				this.addDefaultPos(this.getBlockPos().up());
			else
			{
				for (Direction direction : Direction.values())
				{
					var offState = world.getBlockState(getBlockPos().offset(direction));
					if (offState.isIn(GadgetsBlocks.Tags.GAS_PASS_THROUGH) || !offState.isSolid() && !foundPos)
					{
						this.addDefaultPos(this.getBlockPos());
						foundPos = true;
					}
				}
			}
		}

		if (world instanceof ServerWorld serverWorld)
		{
			blockConcentration.keySet().forEach(pos -> {

				Direction.stream().forEach(direction -> {
					BlockPos offsetPos = pos.offset(direction);
					int originalConcentration = blockConcentration.get(pos);

					BlockState offsetState = world.getBlockState(offsetPos);
					if (blockConcentration.containsKey(offsetPos))
					{
						int offsetConcentration = blockConcentration.get(offsetPos);
						if (originalConcentration > offsetConcentration)
						{
							int concentrationAverage = (originalConcentration - offsetConcentration) / 2;
							int verticalDelta = Math.min(concentrationAverage, 100 - DENSITY);
							int horizontalDelta = Math.min(concentrationAverage, DENSITY);
							int delta = Math.max(Random.create().nextBetween(0, 1), (int)((direction == Direction.UP ? verticalDelta : horizontalDelta) * pressure));
							blockConcentration.replace(pos, originalConcentration - delta);
							blockConcentration.replace(offsetPos, offsetConcentration + delta);
						}
					}
					else if ((offsetState.isIn(GadgetsBlocks.Tags.GAS_PASS_THROUGH) || (!offsetState.isSideSolidFullSquare(world, pos, direction.getOpposite()) && !offsetState.isSideSolidFullSquare(world, pos, direction))) && originalConcentration > 1000 && volume / 1000 > blockConcentration.size())
					{
						blockConcentration.put(offsetPos, 1000);
						blockConcentration.replace(pos, originalConcentration - 1000);

						serverWorld.spawnParticles(particleType,
						                           offsetPos.getX(),
						                           offsetPos.getY(),
						                           offsetPos.getZ(),
						                           Random.create().nextBetween(1, 2),
						                           Random.create().nextBetween(-15, 15) / 100d,
						                           Random.create().nextBetween(-15, 15) / 100d,
						                           Random.create().nextBetween(-15, 15) / 100d,
						                           0);
					}
				});
			});
		}

		if (age > MAX_AGE)
		{
			toxicityIndex.clear();
			this.discard();
		}
	}
}
