package com.parzivail.swg.entity.multipart;

import com.parzivail.util.common.Lumberjack;
import com.parzivail.util.entity.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityMultipart extends Entity
{
	private EntitySeat[] parts;

	private UUID[] searchingSeats;

	public EntityMultipart(World world)
	{
		super(world);
		setSize(1, 2);
	}

	private EntitySeat[] createParts()
	{
		return new EntitySeat[] {
				new EntitySeat(this, "Test seat", SeatRole.Driver)
		};
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		//moveEntity(0.1f, 0, 0);

		//setDead();

		partWatchdog();
	}

	private void partWatchdog()
	{
		if (!worldObj.isRemote)
		{
			if (parts == null)
				parts = createParts();

			for (int i = 0; i < parts.length; i++)
			{
				EntitySeat part = parts[i];
				if (part == null)
				{
					setSeat(searchingSeats[i], i);
					continue;
				}

				part.setLocation();

				if (worldObj.getEntityByID(part.getEntityId()) == null)
					worldObj.spawnEntityInWorld(part);
			}
		}
	}

	@Override
	public void setDead()
	{
		super.setDead();

		if (parts != null)
			for (EntitySeat part : parts)
				part.setDead();
	}

	@Override
	protected void entityInit()
	{

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompound)
	{
		String seatsString = tagCompound.getString("seats");
		String[] seatsPairs = seatsString.split(";");

		parts = new EntitySeat[seatsPairs.length];
		searchingSeats = new UUID[seatsPairs.length];
		for (int i = 0; i < seatsPairs.length; i++)
		{
			String[] pair = seatsPairs[i].split("\\|");
			long lsb = Long.parseLong(pair[0]);
			long msb = Long.parseLong(pair[1]);
			UUID uuid = new UUID(msb, lsb);
			searchingSeats[i] = uuid;
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		if (parts == null)
		{
			tagCompound.setString("seats", "");
			return;
		}

		StringBuilder sb = new StringBuilder();

		for (EntitySeat part : parts)
			sb.append(part.getUniqueID().getLeastSignificantBits()).append("|").append(part.getUniqueID().getMostSignificantBits()).append(";");

		tagCompound.setString("seats", sb.toString());
	}

	public void setSeat(UUID seatId, int seatIdx)
	{
		Entity entity = EntityUtils.getEntityByUuid(worldObj, seatId);

		if (entity == null)
		{
			Lumberjack.warn("Parent failed to locate part with UUID " + seatId);
			//setDead();
		}
		else
		{
			if (!(entity instanceof EntitySeat))
				return;

			parts[seatIdx] = (EntitySeat)entity;
			parts[seatIdx].setParent(getEntityId());
			Lumberjack.warn("Parent located part");
		}
	}
}
