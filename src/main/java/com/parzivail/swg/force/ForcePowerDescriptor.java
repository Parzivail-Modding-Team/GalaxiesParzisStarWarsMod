package com.parzivail.swg.force;

public class ForcePowerDescriptor
{
	private final String id;
	private long cooldownTime;
	private boolean isActive;

	public ForcePowerDescriptor(IForcePower power)
	{
		id = power.getId();
	}

	public String getId()
	{
		return id;
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ForcePowerDescriptor && ((ForcePowerDescriptor)obj).id.equals(id);
	}

	public boolean describes(IForcePower obj)
	{
		return obj.getId().equals(id);
	}

	public void setCooldownTime(long cooldownTime)
	{
		this.cooldownTime = cooldownTime;
	}

	public void setActive(boolean active)
	{
		isActive = active;
	}

	public long getCooldownTime()
	{
		return cooldownTime;
	}

	public boolean isActive()
	{
		return isActive;
	}
}
