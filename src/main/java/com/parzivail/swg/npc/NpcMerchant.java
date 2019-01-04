package com.parzivail.swg.npc;

import com.parzivail.swg.Resources;
import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.util.ai.AiBetterWander;
import com.parzivail.util.ai.AiOpenGate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.HashMap;

public class NpcMerchant extends EntityCreature
{
	public static final HashMap<NpcProfession, ResourceLocation[]> professionSkins = new HashMap<>();
	private static final int DW_PROFESSION = 16;
	private static final int DW_HEIGHT = 17;
	private static final int DW_SKIN = 18;

	static
	{
		professionSkins.put(NpcProfession.Merchant, new ResourceLocation[] {
				Resources.location("textures/npc/moseisleymerchant1.png"),
				Resources.location("textures/npc/moseisleymerchant2.png"),
				Resources.location("textures/npc/moseisleymerchant3.png"),
				});
		professionSkins.put(NpcProfession.Gunsmith, new ResourceLocation[] {
				Resources.location("textures/npc/gunsmith1.png"), Resources.location("textures/npc/gunsmith2.png")
		});
		professionSkins.put(NpcProfession.VehicleSalesman, new ResourceLocation[] {
				Resources.location("textures/npc/vehicledealer.png")
		});
		professionSkins.put(NpcProfession.Bartender, new ResourceLocation[] {
				Resources.location("textures/npc/bartender.png")
		});
		professionSkins.put(NpcProfession.Tailor, new ResourceLocation[] {
				Resources.location("textures/npc/tailor.png")
		});
		professionSkins.put(NpcProfession.Hermit, new ResourceLocation[] {
				Resources.location("textures/npc/hermit1.png"), Resources.location("textures/npc/hermit2.png")
		});
	}

	public NpcMerchant(World world)
	{
		this(world, world.rand.nextInt(professionSkins.size()));
	}

	public NpcMerchant(World world, int profession)
	{
		super(world);
		setProfession(profession);
		setSkin(world.rand.nextInt(professionSkins.get(NpcProfession.fromIndex(profession)).length));
		setHeight(world.rand.nextInt(7));
		setSize(0.6F, 1.8F);
		getNavigator().setBreakDoors(true);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		//tasks.addTask(1, new AiStayWithinBounds(this, ZoneRegistry.zoneExperimentPaddockA, 0.6D));
		tasks.addTask(2, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		tasks.addTask(2, new EntityAIWatchClosest2(this, NpcMerchant.class, 5.0F, 0.02F));
		tasks.addTask(4, new EntityAIOpenDoor(this, true));
		tasks.addTask(4, new AiOpenGate(this, true));
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
	public boolean interact(EntityPlayer player)
	{
		if (isEntityAlive() && !player.isSneaking())
		{
			if (!worldObj.isRemote)
				// yes, I'm passing the entity ID as a position. Sue me.
				player.openGui(StarWarsGalaxy.instance, Resources.GUI_DIALOGUE, worldObj, getEntityId(), 0, 0);

			return true;
		}
		else
		{
			return super.interact(player);
		}
	}

	protected void entityInit()
	{
		super.entityInit();
		dataWatcher.addObject(DW_PROFESSION, 0);
		dataWatcher.addObject(DW_HEIGHT, 0);
		dataWatcher.addObject(DW_SKIN, 0);
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound tagCompound)
	{
		super.writeEntityToNBT(tagCompound);
		tagCompound.setInteger("profession", getProfession());
		tagCompound.setInteger("height", getHeight());
		tagCompound.setInteger("skin", getSkin());
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound tagCompund)
	{
		super.readEntityFromNBT(tagCompund);
		setProfession(tagCompund.getInteger("profession"));
		setHeight(tagCompund.getInteger("height"));
		setSkin(tagCompund.getInteger("skin"));
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

	public int getProfession()
	{
		return dataWatcher.getWatchableObjectInt(DW_PROFESSION);
	}

	public void setProfession(int p_70938_1_)
	{
		dataWatcher.updateObject(DW_PROFESSION, p_70938_1_);
	}

	public int getHeight()
	{
		return dataWatcher.getWatchableObjectInt(DW_HEIGHT);
	}

	public void setHeight(int p_70938_1_)
	{
		dataWatcher.updateObject(DW_HEIGHT, p_70938_1_);
	}

	public int getSkin()
	{
		return dataWatcher.getWatchableObjectInt(DW_SKIN);
	}

	public void setSkin(int p_70938_1_)
	{
		dataWatcher.updateObject(DW_SKIN, p_70938_1_);
	}

	public boolean allowLeashing()
	{
		return false;
	}
}
