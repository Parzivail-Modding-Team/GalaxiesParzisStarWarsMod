package com.parzivail.swg.npc;

import com.parzivail.util.ai.AiBetterWander;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class NpcJawa extends EntityCreature
{
	private static final int DW_HEIGHT = 17;

	public NpcJawa(World world)
	{
		super(world);
		setHeight(world.rand.nextInt(3));
		setSize(0.6F, 1.2F);
		getNavigator().setBreakDoors(true);
		getNavigator().setAvoidsWater(true);
		//tasks.addTask(1, new AiStayWithinBounds(this, ZoneRegistry.zoneExperimentPaddockA, 0.6D));
		tasks.addTask(2, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(2, new EntityAIWatchClosest2(this, NpcJawa.class, 5.0F, 0.02F));
		tasks.addTask(4, new EntityAIOpenDoor(this, true));
		tasks.addTask(5, new AiBetterWander(this, 0.6D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
	}

	/**
	 * Returns true if the newer Entity AI code should be run
	 */
	public boolean isAIEnabled()
	{
		return true;
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
	 */
	public boolean interact(EntityPlayer p_70085_1_)
	{
		if (isEntityAlive() && !p_70085_1_.isSneaking())
		{
			if (!worldObj.isRemote)
			{
				// TODO: display gui
				//p_70085_1_.displayGUIMerchant(this, this.getCustomNameTag());
			}

			return true;
		}
		else
		{
			return super.interact(p_70085_1_);
		}
	}

	protected void entityInit()
	{
		super.entityInit();
		dataWatcher.addObject(DW_HEIGHT, 0);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		super.writeEntityToNBT(tagCompound);
		tagCompound.setInteger("height", getHeight());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund)
	{
		super.readEntityFromNBT(tagCompund);
		setHeight(tagCompund.getInteger("height"));
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	protected boolean canDespawn()
	{
		return false;
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound()
	{
		return "mob.villager.idle";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound()
	{
		return "mob.villager.hit";
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound()
	{
		return "mob.villager.death";
	}

	public int getHeight()
	{
		return dataWatcher.getWatchableObjectInt(DW_HEIGHT);
	}

	public void setHeight(int p_70938_1_)
	{
		dataWatcher.updateObject(DW_HEIGHT, p_70938_1_);
	}

	public boolean allowLeashing()
	{
		return false;
	}
}
