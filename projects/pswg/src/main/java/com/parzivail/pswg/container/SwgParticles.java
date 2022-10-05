package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.pswg.client.particle.ScorchParticle;
import com.parzivail.pswg.client.particle.SlugTrailParticle;
import com.parzivail.pswg.client.particle.SparkParticle;
import com.parzivail.util.client.particle.PParticleType;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SwgParticles
{
	public static final PParticleType SLUG_TRAIL = register(Resources.id("slug_trail"), true, SlugTrailParticle.Factory::new);
	public static final PParticleType SPARK = register(Resources.id("spark"), false, SparkParticle.Factory::new);
	public static final PParticleType SCORCH = register(Resources.id("scorch"), true, ScorchParticle.Factory::new);

	private static PParticleType register(Identifier name, boolean alwaysShow, ParticleFactoryRegistry.PendingParticleFactory<PParticleType> factory)
	{
		var particleType = Registry.register(Registry.PARTICLE_TYPE, name, new PParticleType(alwaysShow));
		ParticleFactoryRegistry.getInstance().register(particleType, factory);
		return particleType;
	}

	public static void register()
	{
	}
}
