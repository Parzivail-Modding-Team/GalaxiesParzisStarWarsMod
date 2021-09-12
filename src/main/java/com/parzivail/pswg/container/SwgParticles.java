package com.parzivail.pswg.container;

import com.parzivail.pswg.Resources;
import com.parzivail.util.client.particle.PParticle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SwgParticles
{
	public static final PParticle SLUG_TRAIL = register(Resources.id("slug_trail"), true);
	public static final PParticle SPARK = register(Resources.id("spark"), false);
	public static final PParticle SCORCH = register(Resources.id("scorch"), true);

	private static PParticle register(Identifier name, boolean alwaysShow)
	{
		return Registry.register(Registry.PARTICLE_TYPE, name, new PParticle(alwaysShow));
	}

	public static void register()
	{
	}
}
