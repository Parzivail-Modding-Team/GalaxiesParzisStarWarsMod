package com.parzivail.swg.entity.fx;

import com.parzivail.swg.Resources;
import com.parzivail.util.fx.AnimatedParticle;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@SideOnly(Side.CLIENT)
public class ParticleSmoke extends AnimatedParticle
{
	private static final ResourceLocation smokeTexture = Resources.location("textures/particle/puff.png");

	public ParticleSmoke(World parWorld, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ)
	{
		super(parWorld, parX, parY, parZ, parMotionX, parMotionY, parMotionZ, smokeTexture, 8, 4);
		particleMaxAge = 20 + parWorld.rand.nextInt(15);
	}
}
