package com.parzivail.swg.entity.multipart;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityMultipart extends Entity
{
	private final EntityPart[] parts;

	public EntityMultipart(World world)
	{
		super(world);
		parts = createParts();
		setSize(1, 2);
	}

	private EntityPart[] createParts()
	{
		return new EntityPart[] {
				new EntityPartTest(this, "Test part", 0.5f, 0.5f)
		};
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		// moveEntity(0, 0, 0);

		for (EntityPart part : parts)
		{
			part.onUpdate();
			part.setLocation();
		}
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompund)
	{

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{

	}

	@Override
	public EntityPart[] getParts()
	{
		return parts;
	}
}
