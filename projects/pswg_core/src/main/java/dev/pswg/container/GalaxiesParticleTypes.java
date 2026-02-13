package dev.pswg.container;

import dev.pswg.Galaxies;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GalaxiesParticleTypes
{
	public static final ParticleType<TintedParticleEffect> SMALL_FLASH_PARTICLE = registerTinted("small_flash");

	public static final SimpleParticleType SHORT_FLAME_PARTICLE = registerSimple("short_flame");
	public static final SimpleParticleType SMALL_SHORT_FLAME_PARTICLE = registerSimple("small_short_flame");

	public static void register()
	{

	}

	private static ParticleType<TintedParticleEffect> registerTinted(String id)
	{
		return Registry.register(Registries.PARTICLE_TYPE, Galaxies.id(id), FabricParticleTypes.complex(true, TintedParticleEffect::createCodec, TintedParticleEffect::createPacketCodec));
	}

	private static SimpleParticleType registerSimple(String id)
	{
		return Registry.register(Registries.PARTICLE_TYPE, Galaxies.id(id), FabricParticleTypes.simple());
	}
}
