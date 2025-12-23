package dev.pswg.entity.gas;

import dev.pswg.container.GadgetsParticleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class SmokeGasEntity extends GasEntity
{

	public SmokeGasEntity(EntityType<?> type, World world)
	{
		super(type, world, 240, 900, 0.8f, 0.9f, GadgetsParticleTypes.SMOKE_PARTICLE);
	}
}
