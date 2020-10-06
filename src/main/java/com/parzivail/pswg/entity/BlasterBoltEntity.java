package com.parzivail.pswg.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlasterBoltEntity extends PersistentProjectileEntity
{
	@Override
	public void writeCustomDataToTag(CompoundTag tag)
	{
		super.writeCustomDataToTag(tag);
		tag.putByte("life", getLife());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag)
	{
		super.readCustomDataFromTag(tag);
		setLife(tag.getByte("life"));
	}

	private static final TrackedData<Byte> LIFE = DataTracker.registerData(BlasterBoltEntity.class, TrackedDataHandlerRegistry.BYTE);

	public BlasterBoltEntity(EntityType<? extends BlasterBoltEntity> type, World world)
	{
		super(type, world);
	}

	public BlasterBoltEntity(EntityType<? extends BlasterBoltEntity> type, LivingEntity owner, World world)
	{
		super(type, owner, world);
	}

	@Override
	public boolean hasNoGravity()
	{
		return true;
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		dataTracker.startTracking(LIFE, (byte) 0);
	}

	private byte getLife()
	{
		return dataTracker.get(LIFE);
	}

	private void setLife(byte life)
	{
		dataTracker.set(LIFE, life);
	}

	@Nullable
	@Override
	protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition)
	{
		return ProjectileUtil.getEntityCollision(this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0D), (entity) -> !entity.isSpectator() && entity.isAlive() && entity.collides() && (entity != this.getOwner() || this.getLife() >= 10));
	}

	@Override
	public void tick()
	{
		this.baseTick();
		final byte life = (byte)(getLife() + 1);
		setLife(life);

		if (life >= 60)
		{
			this.remove();
			return;
		}

		Vec3d velocity = this.getVelocity();
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F)
		{
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}

		BlockPos blockPos = this.getBlockPos();
		BlockState blockState = this.world.getBlockState(blockPos);
		Vec3d vec3d3;
		if (!blockState.isAir())
		{
			VoxelShape voxelShape = blockState.getCollisionShape(this.world, blockPos);
			if (!voxelShape.isEmpty())
			{
				vec3d3 = this.getPos();

				for (Box box : voxelShape.getBoundingBoxes())
				{
					if (box.offset(blockPos).contains(vec3d3))
					{
						this.inGround = true;
						break;
					}
				}
			}
		}

		if (this.inGround)
		{
			remove();
			return;
		} else
		{
			vec3d3 = this.getPos();
			Vec3d vec3d4 = vec3d3.add(velocity);
			HitResult hitResult = this.world.raycast(new RaycastContext(vec3d3, vec3d4, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
			if (hitResult.getType() != HitResult.Type.MISS)
			{
				vec3d4 = hitResult.getPos();
			}

			if (!this.removed)
			{
				EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d4);
				if (entityHitResult != null)
				{
					hitResult = entityHitResult;
				}

				if (hitResult.getType() == HitResult.Type.ENTITY)
				{
					Entity entity = ((EntityHitResult) hitResult).getEntity();
					Entity owner = this.getOwner();
					if (entity instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).shouldDamagePlayer((PlayerEntity) entity))
					{
						hitResult = null;
					}
				}

				if (hitResult != null)
				{
					this.onCollision(hitResult);
					this.velocityDirty = true;
				}
			}

			velocity = this.getVelocity();

			if (this.isTouchingWater())
			{
				remove(); // spawn some smoke particles here
			}

			this.move(MovementType.SELF, velocity);

			this.checkBlockCollision();
		}
	}

	@Override
	protected ItemStack asItemStack()
	{
		return ItemStack.EMPTY;
	}
}
