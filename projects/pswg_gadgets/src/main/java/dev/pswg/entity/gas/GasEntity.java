package dev.pswg.entity.gas;

import com.mojang.serialization.Codec;
import dev.pswg.container.GadgetsBlocks;
import dev.pswg.particle.GasParticleEffect;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class GasEntity extends Entity
{
	private final int DEFAULT_VOLUME;
	public final int MAX_AGE;
	private final float DENSITY;
	private final float DIFFISSION_COEFFICIENT;
	private final ParticleType<GasParticleEffect> PARTICLE_TYPE;

	public ConcurrentMap<BlockPos, Float> massMap;
	public Map<BlockPos, List<String>> particleIdList;
	public int volume;
	public int particlesCreated;

	public GasEntity(EntityType<?> type, Level world, int defaultVolume, int maxAge, float density, float diffusionCoefficient, ParticleType<GasParticleEffect> particle)
	{
		super(type, world);
		DEFAULT_VOLUME = defaultVolume;
		MAX_AGE = maxAge;
		DENSITY = density;
		DIFFISSION_COEFFICIENT = diffusionCoefficient;
		PARTICLE_TYPE = particle;
		volume = DEFAULT_VOLUME;
		massMap = new ConcurrentHashMap<>(1024);
		particleIdList = new ConcurrentHashMap<>();
		particlesCreated = 0;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder)
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
	public boolean hurtServer(ServerLevel world, DamageSource source, float amount)
	{
		return false;
	}

	@Override
	protected void readAdditionalSaveData(ValueInput view)
	{
		var blockPosList = view.read("blockPosList", BlockPos.CODEC.listOf()).get();
		var conList = view.read("concentrationList", Codec.FLOAT.listOf()).get();
		for (int i = 0; i < blockPosList.size(); i++)
			massMap.put(blockPosList.get(i), conList.get(i));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput view)
	{
		byte[] concentrationList = new byte[1024];
		int i = 0;
		for (Float f : massMap.values())
		{
			concentrationList[i] = f.byteValue();
			i++;
		}
		view.store("blockPosList", BlockPos.CODEC.listOf(), massMap.keySet().stream().toList());
		view.putByteArray("concentrationList", concentrationList);
	}

	@Override
	public boolean isNoGravity()
	{
		return true;
	}

	@Override
	public void onClientRemoval()
	{
		super.onClientRemoval();
	}

	public void debug()
	{
		var world = level();
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

		for (Player player : world.players())
		{
			player.sendSystemMessage(Component.nullToEmpty("block count: " + massMap.size() + " minC: " + minConcentration + " maxC: " + maxConcentration + " totalC: " + totalVolume));
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
		var world = level();
		var state = world.getBlockState(blockPosition().above());
		if (state.is(GadgetsBlocks.Tags.GAS_PASS_THROUGH) || !state.isSolid())
			this.addDefaultPos(this.blockPosition().above());
		else
		{
			for (Direction direction : Direction.values())
			{
				var offState = world.getBlockState(blockPosition().relative(direction));
				if (offState.is(GadgetsBlocks.Tags.GAS_PASS_THROUGH) || !offState.isSolid() && !foundPos)
				{
					this.addDefaultPos(this.blockPosition());
					foundPos = true;
				}
			}
		}
	}

	public void updateFlowMap()
	{
		var world = level();
		float totalNeighborPermeability = 0f;
		///  Logic code
		for (Map.Entry<BlockPos, Float> entry : massMap.entrySet())
		{
			BlockPos pos = entry.getKey();
			float mass = Math.max(0, entry.getValue());
			for (Direction dir : Direction.values())
			{
				BlockPos offsetPos = pos.relative(dir);
				BlockState state = world.getBlockState(pos);
				BlockState offsetState = world.getBlockState(offsetPos);

				totalNeighborPermeability += ((!offsetState.isFaceSturdy(world, pos, dir.getOpposite()) && !(state.isFaceSturdy(world, offsetPos, dir)))) ? 1 : 0;
			}

			for (Direction dir : Direction.values())
			{
				BlockPos offsetPos = pos.relative(dir);
				BlockState state = world.getBlockState(pos);
				BlockState offsetState = world.getBlockState(offsetPos);
				float permeability = ((!offsetState.isFaceSturdy(world, pos, dir.getOpposite()) && !(state.isFaceSturdy(world, offsetPos, dir)))) ? 1 : 0;

				if (permeability > 0)
				{
					float offsetMass = massMap.getOrDefault(offsetPos, 0f);
					var massDifferential = totalNeighborPermeability != 0 ? DIFFISSION_COEFFICIENT * (mass - offsetMass) / totalNeighborPermeability : 0;
					if (massDifferential > 0.00625f)
					{
						addFlow(offsetPos, massDifferential);
						mass -= massDifferential;
					}
				}
			}
			if (!massMap.containsKey(pos))
				addFlow(pos, mass);
			else
				massMap.replace(pos, mass);
		}
		/// "Visual" code
		if (this.tickCount % 2 == 0)
		{
			for (BlockPos pos : particleIdList.keySet())
			{
				if (!massMap.containsKey(pos))
					particleIdList.remove(pos);
			}
			for (Map.Entry<BlockPos, Float> entry : massMap.entrySet())
			{
				BlockPos pos = entry.getKey();
				float concentration = entry.getValue();
				float interval = 0.025f;
				float cConc = concentration - interval;
				int particleCount = 0;
				while (cConc > 0)
				{
					cConc -= interval;
					interval += (float)(1f / Math.pow(2, 6));
					particleCount++;
				}

				int particleDelta = particleIdList.containsKey(pos) ? particleCount - particleIdList.get(pos).size() : particleCount;
				if (particleDelta > 0)
				{
					for (int i = 0; i < particleDelta; i++)
					{
						String particleId = this.getStringUUID() + particlesCreated;
						if (world.isClientSide())
						{
							world.addParticle(new GasParticleEffect(PARTICLE_TYPE, this.getStringUUID(), particleId),
							                        true,
							                        true,
							                        pos.getX() + 0.5 + (world.getRandom().nextIntBetweenInclusive(-475, 475) / 1000f),
							                        pos.getY() + 0.5 + (world.getRandom().nextIntBetweenInclusive(-475, 475) / 1000f),
							                        pos.getZ() + 0.5 + (world.getRandom().nextIntBetweenInclusive(-475, 475) / 1000f),
							                        0,
							                        0,
							                        0);
							if (!particleIdList.containsKey(pos))
								particleIdList.put(pos, new ArrayList<>());
							particleIdList.get(pos).add(particleId);
						}
						particlesCreated++;
					}
				}
				if (particleDelta < 0)
				{
					for (int i = 0; i < -particleDelta; i++)
					{
						particleIdList.get(pos).removeLast();
					}
				}
			}
		}
	}

	@Override
	public void tick()
	{
		if (this.tickCount > this.MAX_AGE)
		{
			this.discard();
			return;
		}
		if (this.firstTick)
			setOriginalPos();

		if (!this.firstTick)
		{
			updateFlowMap();
			for (Map.Entry<BlockPos, Float> entry : massMap.entrySet())
				if (entry.getValue() < 0.01f)
					massMap.remove(entry.getKey());
		}
		super.tick();
	}
}
