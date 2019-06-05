package com.parzivail.swg.entity;

import com.parzivail.swg.StarWarsGalaxy;
import com.parzivail.swg.config.SwgConfig;
import com.parzivail.swg.network.client.MessageSetShipInput;
import com.parzivail.swg.proxy.Client;
import com.parzivail.swg.proxy.ShipInputMode;
import com.parzivail.util.common.PNbtUtil;
import com.parzivail.util.math.SlidingWindow;
import com.parzivail.util.math.lwjgl.Matrix4f;
import com.parzivail.util.math.lwjgl.Vector3f;
import com.parzivail.util.math.lwjgl.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class EntityShip extends Entity implements IFreeRotator
{
	public static final ShipInputMode[] shipInputModes = {
			ShipInputMode.Yaw, ShipInputMode.Roll, ShipInputMode.Landing
	};
	private static final DataParameter<Float> THROTTLE = EntityDataManager.createKey(EntityShip.class, DataSerializers.FLOAT);
	private static final DataParameter<Integer> THROTTLE_REPULSOR = EntityDataManager.createKey(EntityShip.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> MODE = EntityDataManager.createKey(EntityShip.class, DataSerializers.VARINT);
	@SideOnly(Side.CLIENT)
	public SlidingWindow slidingPitch = new SlidingWindow(24);
	@SideOnly(Side.CLIENT)
	public SlidingWindow slidingYaw = new SlidingWindow(20);
	@SideOnly(Side.CLIENT)
	public int timeRolled = 0;
	@SideOnly(Side.CLIENT)
	public boolean autoRelevel = false;
	@SideOnly(Side.CLIENT)
	public EntityCamera chaseCam;
	private boolean leftInputDown;
	private boolean rightInputDown;
	private boolean forwardInputDown;
	private boolean backInputDown;
	private Matrix4f prevRotation = new Matrix4f();
	private Matrix4f rotation = new Matrix4f();

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
		this.dataManager.register(THROTTLE, 0f);
		this.dataManager.register(THROTTLE_REPULSOR, 0);
		this.dataManager.register(MODE, 0);

		if (world.isRemote)
		{
			if (chaseCam != null)
				destroyCamera();
			chaseCam = new EntityCamera(world);
			chaseCam.setTarget(this);
			chaseCam.copyLocationAndAnglesFrom(this);
			world.spawnEntity(chaseCam);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound)
	{
		setThrottle(compound.getFloat("throttle"));
		rotation = PNbtUtil.getMatrix4(compound, "rotmat");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound)
	{
		PNbtUtil.setMatrix4(compound, "rotmat", rotation);
		compound.setFloat("throttle", getThrottle());
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
	public boolean canBeCollidedWith()
	{
		return !this.isDead;
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
	public void setDead()
	{
		destroyCamera();
		super.setDead();
	}

	private void destroyCamera()
	{
		if (chaseCam == null)
			return;

		world.removeEntity(chaseCam);
		chaseCam = null;
	}

	@Nullable
	@Override
	public Entity getControllingPassenger()
	{
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : list.get(0);
	}

	public float getEyeHeight()
	{
		return this.height * 0.5f;
	}

	@Override
	public void onUpdate()
	{
		//		this.prevPitch = pitch;
		//		this.prevYaw = yaw;
		//		this.prevRoll = roll;

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		//		this.rotation = dataManager.get(ROTATION);

		super.onUpdate();

		if (world.isRemote && chaseCam == null)
		{
			chaseCam = new EntityCamera(world);
			chaseCam.setTarget(this);
			chaseCam.copyLocationAndAnglesFrom(this);
			world.spawnEntity(chaseCam);
		}

		if (this.canPassengerSteer())
		{
			Entity controllingPassenger = getControllingPassenger();
			if (controllingPassenger instanceof EntityPlayer && this.world.isRemote)
			{
				EntityPlayer pilot = (EntityPlayer)controllingPassenger;
				StarWarsGalaxy.NETWORK.sendToServer(new MessageSetShipInput(this, pilot, rotation));
			}

			this.updateMotion();
		}
		else
		{
			this.motionX = 0.0D;
			this.motionY = 0.0D;
			this.motionZ = 0.0D;
		}

		if (!world.isRemote)
			this.control();
		else
		{
			Vector3f angles = getEulerAngles();

			float currentRoll = angles.z;

			if (Math.abs(currentRoll) > 0.5f)
			{
				timeRolled++;
				if (timeRolled > 20)
					autoRelevel = true;
			}
			else
			{
				timeRolled = 0;
				autoRelevel = false;
			}
		}

		//		slidingPitch.slide(pitch);
		//		slidingYaw.slide(yaw);

		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

		//this.doBlockCollisions();
	}

	public void setInputs(MessageSetShipInput packet)
	{
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotation = rotation;
		this.rotation = packet.rotation;

		Vector3f euler = getEulerAngles();
		this.setRotation(-euler.y, euler.x);

		this.forwardInputDown = packet.forwardInputDown;
		this.backInputDown = packet.backInputDown;
		this.leftInputDown = packet.leftInputDown;
		this.rightInputDown = packet.rightInputDown;
	}

	public Vector3f getEulerAngles()
	{
		Vector4f forward = Matrix4f.transform(rotation, new Vector4f(0, 0, 1, 0), null);
		forward = forward.normalise(null);
		float pitch = (float)(Math.asin(-forward.y) / Math.PI * 180);
		float yaw = (float)(Math.atan2(forward.x, forward.z) / Math.PI * 180);
		float roll = (float)(Math.atan2(rotation.m01, rotation.m11) / Math.PI * 180);
		return new Vector3f(pitch, yaw, roll);
	}

	public void setInputsClient(float mouseDx, float mouseDy, ShipInputMode shipInputMode, boolean forceRelevel)
	{
		this.prevRotationYaw = this.rotationYaw;
		this.prevRotationPitch = this.rotationPitch;
		this.prevRotation = rotation;

		boolean modeAllowsAutoRelevel = false;

		Vector3f angles = getEulerAngles();
		float currentRoll = angles.z;
		float currentPitch = angles.x;

		float coef = SwgConfig.SwgConfigClient.cameraSensitivity;
		mouseDx *= coef;
		mouseDy *= coef;

		switch (shipInputMode)
		{
			case Yaw:
				Matrix4f.rotate((float)(-(mouseDy * 0.15f) / 180 * Math.PI), new Vector3f(1, 0, 0), rotation, rotation);
				Matrix4f.rotate((float)(-(mouseDx * 0.15f) / 180 * Math.PI), new Vector3f(0, 1, 0), rotation, rotation);
				modeAllowsAutoRelevel = true;
				break;
			case Roll:
				Matrix4f.rotate((float)(-(mouseDy * 0.15f) / 180 * Math.PI), new Vector3f(1, 0, 0), rotation, rotation);
				Matrix4f.rotate((float)((mouseDx * 0.15f) / 180 * Math.PI), new Vector3f(0, 0, 1), rotation, rotation);
				break;
			case Landing:
				Matrix4f.rotate((float)((-currentPitch * 0.01f) / 180 * Math.PI), new Vector3f(1, 0, 0), rotation, rotation);
				Matrix4f.rotate((float)(-(mouseDx * 0.15f) / 180 * Math.PI), new Vector3f(0, 1, 0), rotation, rotation);
				Matrix4f.rotate((float)((-currentRoll * 0.01f) / 180 * Math.PI), new Vector3f(0, 0, 1), rotation, rotation);
				break;
		}

		boolean shouldAttemptAutoRelevel = (autoRelevel && Client.autoRelevelEnabled && modeAllowsAutoRelevel);
		if (forceRelevel || shouldAttemptAutoRelevel)
			Matrix4f.rotate((float)((-currentRoll * 0.01f) / 180 * Math.PI), new Vector3f(0, 0, 1), rotation, rotation);

		if (shouldAttemptAutoRelevel && Math.abs(currentRoll) < 0.5)
		{
			timeRolled = 0;
			autoRelevel = false;
		}

		Vector3f euler = getEulerAngles();
		this.setRotation(-euler.y, euler.x);
	}

	private void control()
	{
		if (this.isBeingRidden())
		{
			ShipInputMode inputMode = shipInputModes[getInputMode()];
			if (inputMode == ShipInputMode.Landing)
			{
				if (getThrottle() != 0)
					setThrottle(0);

				if (this.forwardInputDown)
				{
					setThrottleRepulsor(1);
				}
				else if (this.backInputDown)
				{
					setThrottleRepulsor(-1);
				}
				else
					setThrottleRepulsor(0);
			}
			else
			{
				if (this.forwardInputDown)
				{
					float throttle = getThrottle();
					throttle = MathHelper.clamp(throttle + 0.1f, 0, 1);
					setThrottle(throttle);
				}
				else if (this.backInputDown)
				{
					float throttle = getThrottle();
					throttle = MathHelper.clamp(throttle - 0.1f, 0, 1);
					setThrottle(throttle);
				}
			}
		}
	}

	public Matrix4f getRotation()
	{
		return rotation;
	}

	private float getThrottle()
	{
		return dataManager.get(THROTTLE);
	}

	private void setThrottle(float throttle)
	{
		dataManager.set(THROTTLE, throttle);
	}

	private int getThrottleRepulsor()
	{
		return dataManager.get(THROTTLE_REPULSOR);
	}

	private void setThrottleRepulsor(int throttle)
	{
		dataManager.set(THROTTLE_REPULSOR, throttle);
	}

	public int getInputMode()
	{
		return dataManager.get(MODE);
	}

	public void setInputMode(int mode)
	{
		dataManager.set(MODE, mode);
	}

	private void updateMotion()
	{
		double d1 = this.hasNoGravity() ? 0.0D : -0.03999999910593033D;

		//this.motionY += d1;

		ShipInputMode mode = shipInputModes[getInputMode()];
		if (mode == ShipInputMode.Landing)
		{
			float throttleRepulsor = getThrottleRepulsor();
			this.motionX = 0;
			this.motionY = throttleRepulsor * 0.25f;
			this.motionZ = 0;
		}
		else
		{
			float throttle = getThrottle();
			if (throttle > 0)
			{
				Vector4f forward = getForwardVector();

				this.motionX = forward.x * throttle * 4;
				this.motionY = forward.y * throttle * 4;
				this.motionZ = forward.z * throttle * 4;
			}
			else
			{
				this.motionX = 0;
				this.motionY = 0;
				this.motionZ = 0;
			}
		}
	}

	private Vector4f getForwardVector()
	{
		Matrix4f rotatedAxes = getRotation();
		return Matrix4f.transform(rotatedAxes, new Vector4f(0, 0, 1, 0), null);
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

	@Override
	public Matrix4f getRotationMatrix()
	{
		return rotation;
	}
}
