package dev.pswg.container;

import dev.pswg.Gadgets;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GadgetsParticleTypes
{
	public static final SimpleParticleType EXPLOSION_SMOKE_PARTICLE = registerSimple("explosion_smoke");

	public static final SimpleParticleType FRAGMENTATION_GRENADE_SPARK_PARTICLE = registerSimple("fragmentation_grenade_spark");
	public static final SimpleParticleType FRAGMENTATION_GRENADE_WAVE_PARTICLE = registerSimple("fragmentation_grenade_wave");

	public static final SimpleParticleType SMOKE_PARTICLE = registerSimple("smoke");
	public static final SimpleParticleType NERVE_GAS_PARTICLE = registerSimple("nerve_gas");

	public static void register()
	{

	}

	private static SimpleParticleType registerSimple(String id)
	{
		return Registry.register(Registries.PARTICLE_TYPE, Gadgets.id(id), FabricParticleTypes.simple());
	}
}
