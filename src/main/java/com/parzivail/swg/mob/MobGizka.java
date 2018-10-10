package com.parzivail.swg.mob;

import com.parzivail.swg.Resources;
import com.parzivail.swg.registry.ItemRegister;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MobGizka extends EntityAnimal
{
	public float field_70886_e;
	public float destPos;
	public float field_70884_g;
	public float field_70888_h;
	public float field_70889_i = 1.0F;

	public MobGizka(World p_i1682_1_)
	{
		super(p_i1682_1_);
		setSize(0.3F, 0.7F);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 1.4D));
		tasks.addTask(2, new EntityAIMate(this, 1.0D));
		tasks.addTask(3, new EntityAITempt(this, 1.0D, ItemRegister.joganFruit, false));
		tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
		tasks.addTask(5, new EntityAIWander(this, 1.0D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
	}

	/**
	 * Returns true if the newer Entity AI code should be run
	 */
	public boolean isAIEnabled()
	{
		return true;
	}

	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
		field_70888_h = field_70886_e;
		field_70884_g = destPos;
		destPos = (float)((double)destPos + (double)(onGround ? -1 : 4) * 0.3D);

		if (destPos < 0.0F)
		{
			destPos = 0.0F;
		}

		if (destPos > 1.0F)
		{
			destPos = 1.0F;
		}

		if (!onGround && field_70889_i < 1.0F)
		{
			field_70889_i = 1.0F;
		}

		field_70889_i = (float)((double)field_70889_i * 0.9D);
		field_70886_e += field_70889_i * 2.0F;
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound()
	{
		return Resources.modColon("mob.gizka.say");
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound()
	{
		return Resources.modColon("mob.gizka.hurt");
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	protected String getDeathSound()
	{
		return Resources.modColon("mob.gizka.hurt");
	}

	protected void playStepSound(int x, int y, int z, Block blockIn)
	{
		playSound(Resources.modColon("mob.gizka.step"), 0.15F, 1.0F);
	}

	/**
	 * Drop 0-2 items of this living's type
	 */
	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_)
	{
		int j = rand.nextInt(3) + rand.nextInt(1 + p_70628_2_);

		for (int k = 0; k < j; ++k)
			dropItem(Items.leather, 1);

		if (isBurning())
			dropItem(ItemRegister.gizkaSteak, 1);
		else
			dropItem(ItemRegister.gizkaChop, 1);
	}

	public MobGizka createChild(EntityAgeable p_90011_1_)
	{
		return new MobGizka(worldObj);
	}

	/**
	 * Checks if the parameter is an item which this animal can be fed to breed it (wheat, carrots or seeds depending on
	 * the animal type)
	 */
	public boolean isBreedingItem(ItemStack p_70877_1_)
	{
		return p_70877_1_ != null && p_70877_1_.getItem() == ItemRegister.joganFruit;
	}
}
