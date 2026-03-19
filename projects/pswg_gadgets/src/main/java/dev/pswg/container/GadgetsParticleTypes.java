package dev.pswg.container;

import dev.pswg.Gadgets;
import dev.pswg.particle.GasParticleEffect;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class GadgetsParticleTypes
{
	public static final SimpleParticleType EXPLOSION_SMOKE_PARTICLE = registerSimple("explosion_smoke");
	public static final SimpleParticleType FRAGMENTATION_GRENADE_SPARK_PARTICLE = registerSimple("fragmentation_grenade_spark");
	public static final SimpleParticleType FRAGMENTATION_GRENADE_WAVE_PARTICLE = registerSimple("fragmentation_grenade_wave");

	public static final SimpleParticleType TRIPWIRE_LASER_PARTICLE = registerSimple("tripwire_laser");

	public static final SimpleParticleType INFERNO_SCORCH_PARTICLE = registerSimple("inferno_scorch");
	public static final SimpleParticleType DENSE_INFERNO_SCORCH_PARTICLE = registerSimple("dense_inferno_scorch");

	public static final SimpleParticleType LASER_CUT_PARTICLE = registerSimple("laser_cut");

	public static final ParticleType<GasParticleEffect> SMOKE_PARTICLE = registerGas("smoke");
	public static final ParticleType<GasParticleEffect> NERVE_GAS_PARTICLE = registerGas("nerve_gas");

	public static void register()
	{

	}

	private static ParticleType<ColorParticleOption> registerTinted(String id)
	{
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Gadgets.id(id), FabricParticleTypes.complex(true, ColorParticleOption::codec, ColorParticleOption::streamCodec));
	}

	private static SimpleParticleType registerSimple(String id)
	{
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Gadgets.id(id), FabricParticleTypes.simple());
	}

	private static ParticleType<GasParticleEffect> registerGas(String id)
	{
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Gadgets.id(id), FabricParticleTypes.complex(true, GasParticleEffect::createCodec, GasParticleEffect::createPacketCodec));
	}
}
