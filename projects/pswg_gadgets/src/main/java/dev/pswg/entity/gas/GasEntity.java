package dev.pswg.entity.gas;

import com.mojang.serialization.Codec;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.particle.GasParticleEffect;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GasEntity extends Entity
{
	private final int DEFAULT_VOLUME;
	public final int MAX_AGE;
	private final float DENSITY;
	private final float DIFFISSION_COEFFICIENT;
	private final ParticleType<GasParticleEffect> PARTICLE_TYPE;

	//public ConcurrentMap<BlockPos, Float> blockConcentration;
	public ConcurrentMap<BlockPos, Float> massMap;
	public int volume;

	public GasEntity(EntityType<?> type, World world, int defaultVolume, int maxAge, float density, float diffusionCoefficient, ParticleType<GasParticleEffect> particle)
	{
		super(type, world);
		DEFAULT_VOLUME = defaultVolume;
		MAX_AGE = maxAge;
		DENSITY = density;
		DIFFISSION_COEFFICIENT = diffusionCoefficient;
		PARTICLE_TYPE = particle;
		volume = DEFAULT_VOLUME;
		massMap = new ConcurrentHashMap<>(1024);
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
		this.massMap.put(originalPos, (float)volume);
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount)
	{
		return false;
	}

	@Override
	protected void readCustomData(ReadView view)
	{
		var xList = view.getOptionalIntArray("xList").get();
		var yList = view.getOptionalIntArray("yList").get();
		var zList = view.getOptionalIntArray("ZList").get();
		var conList = view.read("concentrationList", Codec.FLOAT.listOf());
		int s = xList.length;
		for (int i = 0; i < s; i++)
			massMap.put(new BlockPos(xList[i], yList[i], zList[i]), conList.get().get(i));
	}

	@Override
	protected void writeCustomData(WriteView view)
	{
		int[] xList = new int[1024];
		int[] yList = new int[1024];
		int[] zList = new int[1024];

		int i = 0;
		for (BlockPos blockPos : massMap.keySet())
		{
			xList[i] = blockPos.getX();
			yList[i] = blockPos.getY();
			zList[i] = blockPos.getZ();
			i++;
		}
		byte[] concentrationList = new byte[1024];
		i = 0;
		for (Float f : massMap.values())
		{
			concentrationList[i] = f.byteValue();
			i++;
		}
		view.putIntArray("xList", xList);
		view.putIntArray("yList", yList);
		view.putIntArray("zList", zList);
		view.putByteArray("concentrationList", concentrationList);
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
		var world = getEntityWorld();
		float totalVolume = 0;
		float maxConcentration = -1;
		float minConcentration = Math.max(maxConcentration, DEFAULT_VOLUME);
		for (float f : massMap.values())
		{
			totalVolume += f;
			if (maxConcentration < f)
				maxConcentration = f;
			if (minConcentration > f)
				minConcentration = f;
		}

		for (PlayerEntity player : world.getPlayers())
		{
			player.sendMessage(Text.of("block count: " + massMap.size() + " minC: " + minConcentration + " maxC: " + maxConcentration + " totalC: " + totalVolume), false);
		}
	}

	public void addFlow(BlockPos pos, float massDifferential)
	{
		if (massMap.containsKey(pos))
			massMap.replace(pos, massMap.get(pos) + massDifferential);
		else
			massMap.put(pos, massDifferential);
	}

	public void setOriginalPos()
	{
		boolean foundPos = false;
		var world = getEntityWorld();
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

	public void updateFlowMap()
	{
		var world = getEntityWorld();
		float totalNeighborPermeability = 0f;
		for (Map.Entry<BlockPos, Float> entry : massMap.entrySet())
		{
			BlockPos pos = entry.getKey();
			float mass = Math.max(0, entry.getValue());
			for (Direction dir : Direction.values())
			{
				BlockPos offsetPos = pos.offset(dir);
				BlockState state = world.getBlockState(pos);
				BlockState offsetState = world.getBlockState(offsetPos);

				totalNeighborPermeability += ((!offsetState.isSideSolidFullSquare(world, pos, dir.getOpposite()) && !(state.isSideSolidFullSquare(world, offsetPos, dir)))) ? 1 : 0;
			}
			;

			for (Direction dir : Direction.values())
			{
				BlockPos offsetPos = pos.offset(dir);
				BlockState state = world.getBlockState(pos);
				BlockState offsetState = world.getBlockState(offsetPos);
				float permeability = ((!offsetState.isSideSolidFullSquare(world, pos, dir.getOpposite()) && !(state.isSideSolidFullSquare(world, offsetPos, dir)))) ? 1 : 0;

				if (permeability > 0)
				{
					float offsetMass = massMap.getOrDefault(offsetPos, 0f);
					var massDifferential = totalNeighborPermeability != 0 ? DIFFISSION_COEFFICIENT * (mass - offsetMass) / totalNeighborPermeability : 0;
					if (massDifferential > 0)
					{
						addFlow(offsetPos, massDifferential);
						mass -= massDifferential;
					}
					if (massMap.containsKey(offsetPos))
					{
						if ((massMap.get(offsetPos) - massDifferential) / 0.5f != massMap.get(offsetPos) / 0.5f && massDifferential > 0)
						{
							if (world.isClient())
								world.addParticleClient(new GasParticleEffect(PARTICLE_TYPE, this.getId(), massMap.getOrDefault(offsetPos, 0.5f) - (massMap.getOrDefault(offsetPos, 0.5f) % 0.5f)),
								                  true,
								                  true,
								                  offsetPos.getX() + 0.5 + (world.random.nextBetween(-450, 450) / 1000f),
								                  offsetPos.getY() + 0.5 + (world.random.nextBetween(-450, 450) / 1000f),
								                  offsetPos.getZ() + 0.5 + (world.random.nextBetween(-450, 450) / 1000f),
								                  0,
								                  0,
								                  0);
						}
					}
				}
			}
			;
			if (!massMap.containsKey(pos))
				addFlow(pos, mass);
			else
				massMap.replace(pos, mass);
		}
	}

	@Override
	public void tick()
	{
		if (this.firstUpdate)
			setOriginalPos();

		if (!this.firstUpdate)
		{
			updateFlowMap();
			for (Map.Entry<BlockPos, Float> entry : massMap.entrySet())
				if (entry.getValue() < 0.01f)
					massMap.remove(entry.getKey());
		}
		super.tick();
	}
}
