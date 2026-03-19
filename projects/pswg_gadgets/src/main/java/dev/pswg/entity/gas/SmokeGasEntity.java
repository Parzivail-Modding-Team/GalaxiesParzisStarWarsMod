package dev.pswg.entity.gas;

import dev.pswg.container.GadgetsParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class SmokeGasEntity extends GasEntity
{

	public SmokeGasEntity(EntityType<?> type, Level world)
	{
		super(type, world, 240, 900, 0.8f, 0.9f, GadgetsParticleTypes.SMOKE_PARTICLE);
	}
}
