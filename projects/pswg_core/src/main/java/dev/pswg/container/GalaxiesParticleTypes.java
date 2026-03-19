package dev.pswg.container;

import dev.pswg.Galaxies;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public class GalaxiesParticleTypes
{
	public static final ParticleType<ColorParticleOption> SMALL_FLASH_PARTICLE = registerTinted("small_flash");

	public static final SimpleParticleType SHORT_FLAME_PARTICLE = registerSimple("short_flame");
	public static final SimpleParticleType SMALL_SHORT_FLAME_PARTICLE = registerSimple("small_short_flame");

	public static void register()
	{

	}

	private static ParticleType<ColorParticleOption> registerTinted(String id)
	{
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Galaxies.id(id), FabricParticleTypes.complex(true, ColorParticleOption::codec, ColorParticleOption::streamCodec));
	}

	private static SimpleParticleType registerSimple(String id)
	{
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, Galaxies.id(id), FabricParticleTypes.simple());
	}
}
