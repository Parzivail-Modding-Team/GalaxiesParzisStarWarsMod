package dev.pswg.entity.gas;

import dev.pswg.container.GadgetsParticleTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class SmokeGasEntity extends GasEntity
{

	public SmokeGasEntity(EntityType<?> type, World world)
	{
		super(type, world, 80000, 900, 90, GadgetsParticleTypes.SMOKE_PARTICLE);
	}
}
