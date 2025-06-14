package dev.pswg.entity.gas;

import dev.pswg.container.GadgetsParticleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class SmokeGasEntity extends GasEntity
{

	public SmokeGasEntity(EntityType<?> type, World world)
	{
		super(type, world, 80, 900, 0.8f, GadgetsParticleTypes.SMOKE_PARTICLE);
	}
}
