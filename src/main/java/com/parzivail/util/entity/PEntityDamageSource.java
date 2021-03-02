package com.parzivail.util.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.EntityDamageSource;
import org.jetbrains.annotations.Nullable;

public class PEntityDamageSource extends EntityDamageSource
{
	private boolean ignoresInvulnerableFrames;

	public PEntityDamageSource(String name, @Nullable Entity source)
	{
		super(name, source);
	}

	public PEntityDamageSource setIgnoresInvulnerableFrames()
	{
		this.ignoresInvulnerableFrames = true;
		return this;
	}

	public boolean ignoresInvulnerableFrames()
	{
		return ignoresInvulnerableFrames;
	}
}
