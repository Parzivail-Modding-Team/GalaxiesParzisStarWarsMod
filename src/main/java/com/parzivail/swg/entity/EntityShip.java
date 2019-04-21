package com.parzivail.swg.entity;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.network.client.MessageSetShipInput;
import com.parzivail.util.math.RotatedAxes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class EntityShip extends Entity
{
	private static final DataParameter<Rotations> ROTATION = EntityDataManager.createKey(EntityBoat.class, DataSerializers.ROTATIONS);

	private boolean leftInputDown;
	private boolean rightInputDown;
	private boolean forwardInputDown;
	private boolean backInputDown;

	public EntityShip(World worldIn)
	{
		super(worldIn);
		this.setSize(1, 1);
	}

	public EntityShip(World worldIn, double x, double y, double z)
	{
		this(worldIn);
		this.setPosition(x, y, z);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}

	@Override
	protected void entityInit()
	{
		this.dataManager.register(ROTATION, new Rotations(0, 0, 0));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		dataManager.set(ROTATION, new Rotations(compound.getTagList("rotation", 5)));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		compound.setTag("rotation", dataManager.get(ROTATION).writeToNBT());
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn)
	{
		return entityIn.getEntityBoundingBox();
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox()
	{
		return this.getEntityBoundingBox();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (!this.world.isRemote && !this.isDead)
		{
			if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && this.isPassenger(source.getTrueSource()))
				return false;
			else
			{
				this.setDead();
				return true;
			}
		}
		else
			return true;
	}

	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		super.onUpdate();

		if (this.canPassengerSteer())
		{
			this.updateMotion();

			if (this.world.isRemote)
			{
				this.control();
				StarWarsGalaxy.NETWORK.sendToServer(new MessageSetShipInput(this, this.forwardInputDown, this.backInputDown, this.leftInputDown, this.rightInputDown));
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		}
		else
		{
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}

		//this.doBlockCollisions();
	}

	public void setInputs(boolean forwardInputDown, boolean backInputDown, boolean leftInputDown, boolean rightInputDown)
	{
		this.forwardInputDown = forwardInputDown;
		this.backInputDown = backInputDown;
		this.leftInputDown = leftInputDown;
		this.rightInputDown = rightInputDown;
	}

	private void control()
	{
		if (this.isBeingRidden())
		{
			float f = 0.0F;

			if (this.leftInputDown)
			{
				rotateYaw(0.04f);
			}

			if (this.rightInputDown)
			{
				rotateYaw(-0.04f);
			}

			if (this.forwardInputDown)
			{
				rotatePitch(-0.04f);
			}

			if (this.backInputDown)
			{
				rotatePitch(0.04f);
			}

			this.motionX += (double)(MathHelper.sin(-this.rotationYaw * 0.017453292F) * f);
			this.motionZ += (double)(MathHelper.cos(this.rotationYaw * 0.017453292F) * f);
		}
	}

	private void rotateYaw(float amount)
	{
		changeOrientation(axes -> axes.rotateLocalYaw(amount));
	}

	private void rotatePitch(float amount)
	{
		changeOrientation(axes -> axes.rotateLocalPitch(amount));
	}

	private void changeOrientation(Consumer<RotatedAxes> func)
	{
		Rotations angles = dataManager.get(ROTATION);
		RotatedAxes axes = new RotatedAxes(angles.getY(), angles.getX(), angles.getZ());
		func.accept(axes);
		dataManager.set(ROTATION, new Rotations(axes.getPitch(), axes.getYaw(), axes.getRoll()));
	}

	private void updateMotion()
	{
		double d1 = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;

		//this.motionY += d1;
	}

	@Override
	public void updatePassenger(Entity passenger)
	{
		if (this.isPassenger(passenger))
		{
			passenger.setPosition(this.posX, this.posY, this.posZ);
			this.applyYawToEntity(passenger);
		}
	}

	protected void applyYawToEntity(Entity entityToUpdate)
	{
		entityToUpdate.setRenderYawOffset(this.rotationYaw);
		float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
		float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
		entityToUpdate.prevRotationYaw += f1 - f;
		entityToUpdate.rotationYaw += f1 - f;
		entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
	}

	/**
	 * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public void applyOrientationToEntity(Entity entityToUpdate)
	{
		this.applyYawToEntity(entityToUpdate);
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
	{
		if (player.isSneaking())
			return false;
		else
		{
			if (!this.world.isRemote)
				player.startRiding(this);

			return true;
		}
	}
}
