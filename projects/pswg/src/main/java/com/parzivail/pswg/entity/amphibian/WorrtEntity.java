package com.parzivail.pswg.entity.amphibian;

import com.parzivail.util.entity.EntityUtil;
import com.parzivail.util.math.MathUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WorrtEntity extends AnimalEntity
{
	private int jumpTicks;
	private int jumpDuration;
	private boolean lastOnGround;
	private int ticksUntilJump;

	public WorrtEntity(EntityType<? extends WorrtEntity> entityType, World world)
	{
		super(entityType, world);
		this.jumpControl = new WorrtJumpControl(this);
		this.moveControl = new WorrtEntity.WorrtMoveControl(this);
		this.setSpeed(0.0D);
	}

	@Override
	protected void initGoals()
	{
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(1, new WorrtEntity.EscapeDangerGoal(this, 0.5D));
		this.goalSelector.add(4, new FleeEntityGoal<>(this, PlayerEntity.class, 8.0F, 0.3D, 0.7D));
		this.goalSelector.add(4, new FleeEntityGoal<>(this, HostileEntity.class, 4.0F, 0.3D, 0.7D));
		this.goalSelector.add(6, new WanderAroundGoal(this, 0.7D, 5000));
		this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 10.0F));
	}

	public static DefaultAttributeContainer.Builder createAttributes()
	{
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0D);
	}

	@Override
	protected float getJumpVelocity()
	{
		if (!this.horizontalCollision && (!this.moveControl.isMoving() || !(this.moveControl.getTargetY() > this.getY() + 0.5D)))
		{
			var path = this.navigation.getCurrentPath();
			if (path != null && !path.isFinished())
			{
				var vec3d = path.getNodePosition(this);
				if (vec3d.y > this.getY() + 0.5D)
				{
					return 0.5F;
				}
			}

			return (float)this.moveControl.getSpeed() * 0.5f;
		}
		else
		{
			return 0.75F;
		}
	}

	@Override
	protected void jump()
	{
		super.jump();
		var d = this.moveControl.getSpeed();
		if (d > 0.0D)
		{
			var e = this.getVelocity().horizontalLengthSquared();
			if (e < 0.01D)
			{
				this.updateVelocity((float)d, new Vec3d(0.0D, 0.0D, 0.5D));
			}
		}

		if (!this.world.isClient)
		{
			this.world.sendEntityStatus(this, (byte)1);
		}
	}

	public void setSpeed(double speed)
	{
		this.getNavigation().setSpeed(speed);
		this.moveControl.moveTo(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ(), speed);
	}

	@Override
	public void setJumping(boolean jumping)
	{
		super.setJumping(jumping);
//		if (jumping)
//		{
//			this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
//		}
	}

	public void startJump()
	{
		this.setJumping(true);
		this.jumpDuration = 10;
		this.jumpTicks = 0;
	}

	@Override
	public void mobTick()
	{
		if (this.ticksUntilJump > 0)
		{
			--this.ticksUntilJump;
		}

		if (this.onGround)
		{
			if (!this.lastOnGround)
			{
				this.setJumping(false);
				this.scheduleJump();
			}

			var worrtJumpControl = (WorrtEntity.WorrtJumpControl)this.jumpControl;
			if (!worrtJumpControl.isActive())
			{
				if (this.moveControl.isMoving() && this.ticksUntilJump == 0)
				{
					var path = this.navigation.getCurrentPath();
					var vec3d = new Vec3d(this.moveControl.getTargetX(), this.moveControl.getTargetY(), this.moveControl.getTargetZ());
					if (path != null && !path.isFinished())
					{
						vec3d = path.getNodePosition(this);
					}

					this.lookTowards(vec3d.x, vec3d.z);
					this.startJump();
				}
			}
			else if (!worrtJumpControl.method_27313())
			{
				this.method_6611();
			}
		}

		this.lastOnGround = this.onGround;
	}

	@Environment(EnvType.CLIENT)
	public float getAirborneLerp(float tickDelta)
	{
		var pos = this.getLerpedPos(tickDelta);
		var hit = EntityUtil.raycastBlocks(pos, MathUtil.V3D_NEG_Y, 1f, this, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE);
		if (hit.getType() == HitResult.Type.MISS)
			return 1;

		return (float)MathHelper.clamp(hit.getPos().distanceTo(pos), 0, 1);
	}

	@Override
	public boolean shouldSpawnSprintingParticles()
	{
		return false;
	}

	private void lookTowards(double x, double z)
	{
		this.setYaw((float)(MathHelper.atan2(z - this.getZ(), x - this.getX()) * 57.2957763671875D) - 90.0F);
	}

	private void method_6611()
	{
		((WorrtEntity.WorrtJumpControl)this.jumpControl).method_27311(true);
	}

	private void method_6621()
	{
		((WorrtEntity.WorrtJumpControl)this.jumpControl).method_27311(false);
	}

	private void doScheduleJump()
	{
		if (this.moveControl.getSpeed() < 2.2D)
		{
			this.ticksUntilJump = 10;
		}
		else
		{
			this.ticksUntilJump = 1;
		}
	}

	private void scheduleJump()
	{
		this.doScheduleJump();
		this.method_6621();
	}

	@Override
	public void tickMovement()
	{
		super.tickMovement();
		if (this.jumpTicks != this.jumpDuration)
		{
			++this.jumpTicks;
		}
		else if (this.jumpDuration != 0)
		{
			this.jumpTicks = 0;
			this.jumpDuration = 0;
			this.setJumping(false);
		}
	}

	@Override
	public WorrtEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity)
	{
		return null;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void handleStatus(byte status)
	{
		if (status == 1)
		{
			this.spawnSprintingParticles();
			this.jumpDuration = 10;
			this.jumpTicks = 0;
		}
		else
		{
			super.handleStatus(status);
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	public Vec3d getLeashOffset()
	{
		return new Vec3d(0.0D, 0.6F * this.getStandingEyeHeight(), this.getWidth() * 0.4F);
	}

	static class EscapeDangerGoal extends net.minecraft.entity.ai.goal.EscapeDangerGoal
	{
		private final WorrtEntity worrt;

		public EscapeDangerGoal(WorrtEntity worrt, double speed)
		{
			super(worrt, speed);
			this.worrt = worrt;
		}

		@Override
		public void tick()
		{
			super.tick();
			this.worrt.setSpeed(this.speed);
		}
	}

	static class WorrtMoveControl extends MoveControl
	{
		private final WorrtEntity worrt;
		private double worrtSpeed;

		public WorrtMoveControl(WorrtEntity owner)
		{
			super(owner);
			this.worrt = owner;
		}

		@Override
		public void tick()
		{
			if (this.worrt.onGround && !this.worrt.jumping && !((WorrtEntity.WorrtJumpControl)this.worrt.jumpControl).isActive())
			{
				this.worrt.setSpeed(0.0D);
			}
			else if (this.isMoving())
			{
				this.worrt.setSpeed(this.worrtSpeed);
			}

			super.tick();
		}

		@Override
		public void moveTo(double x, double y, double z, double speed)
		{
			if (this.worrt.isTouchingWater())
			{
				speed = 0.5D;
			}

			super.moveTo(x, y, z, speed);
			if (speed > 0.0D)
			{
				this.worrtSpeed = speed;
			}
		}
	}

	public static class WorrtJumpControl extends JumpControl
	{
		private final WorrtEntity worrt;
		private boolean field_24091;

		public WorrtJumpControl(WorrtEntity worrt)
		{
			super(worrt);
			this.worrt = worrt;
		}

		public boolean isActive()
		{
			return this.active;
		}

		public boolean method_27313()
		{
			return this.field_24091;
		}

		public void method_27311(boolean bl)
		{
			this.field_24091 = bl;
		}

		@Override
		public void tick()
		{
			if (this.active)
			{
				this.worrt.startJump();
				this.active = false;
			}
		}
	}
}
