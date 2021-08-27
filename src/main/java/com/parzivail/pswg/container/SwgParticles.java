package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.util.client.particle.PParticle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SwgParticles
{
	public static final PParticle SLUG_TRAIL = register(Resources.id("slug_trail"), true);

	private static PParticle register(Identifier name, boolean alwaysShow)
	{
		return Registry.register(Registry.PARTICLE_TYPE, name, new PParticle(alwaysShow));
	}

	public static void register()
	{
		// Op
	}
}
