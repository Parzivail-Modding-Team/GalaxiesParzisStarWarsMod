package com.parzivail.util.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.ProjectileDamageSource;

public class PProjectileEntityDamageSource extends ProjectileDamageSource
{
	private boolean ignoresInvulnerableFrames;

	public PProjectileEntityDamageSource(String name, Entity projectile, Entity source)
	{
		super(name, projectile, source);
	}

	public PProjectileEntityDamageSource setIgnoresInvulnerableFrames()
	{
		this.ignoresInvulnerableFrames = true;
		return this;
	}

	public boolean ignoresInvulnerableFrames()
	{
		return ignoresInvulnerableFrames;
	}
}
