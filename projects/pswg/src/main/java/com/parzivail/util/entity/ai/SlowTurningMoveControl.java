package com.parzivail.util.entity.ai;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class SlowTurningMoveControl extends MoveControl
{
	private final float maxYawRate;

	public SlowTurningMoveControl(MobEntity entity, float maxYawRate)
	{
		super(entity);
		this.maxYawRate = maxYawRate;
	}

	private boolean isPathWalkable(float dX, float dZ)
	{
		EntityNavigation entityNavigation = this.entity.getNavigation();
		if (entityNavigation != null)
		{
			PathNodeMaker pathNodeMaker = entityNavigation.getNodeMaker();
			return pathNodeMaker == null
			       || pathNodeMaker.getDefaultNodeType(
					this.entity.getWorld(),
					MathHelper.floor(this.entity.getX() + (double)dX),
					this.entity.getBlockY(),
					MathHelper.floor(this.entity.getZ() + (double)dZ)
			) == PathNodeType.WALKABLE;
		}

		return true;
	}

	@Override
	public void tick()
	{
		if (this.state == SlowTurningMoveControl.State.STRAFE)
		{
			float f = (float)this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			float g = (float)this.speed * f;
			float h = this.forwardMovement;
			float i = this.sidewaysMovement;
			float j = MathHelper.sqrt(h * h + i * i);
			if (j < 1.0F)
			{
				j = 1.0F;
			}

			j = g / j;
			h *= j;
			i *= j;
			float k = MathHelper.sin(this.entity.getYaw() * (float)(Math.PI / 180.0));
			float l = MathHelper.cos(this.entity.getYaw() * (float)(Math.PI / 180.0));
			float m = h * l - i * k;
			float n = i * l + h * k;
			if (!this.isPathWalkable(m, n))
			{
				this.forwardMovement = 1.0F;
				this.sidewaysMovement = 0.0F;
			}

			this.entity.setMovementSpeed(g);
			this.entity.setForwardSpeed(this.forwardMovement);
			this.entity.setSidewaysSpeed(this.sidewaysMovement);
			this.state = SlowTurningMoveControl.State.WAIT;
		}
		else if (this.state == SlowTurningMoveControl.State.MOVE_TO)
		{
			this.state = SlowTurningMoveControl.State.WAIT;
			double f = this.targetX - this.entity.getX();
			double h = this.targetZ - this.entity.getZ();
			double j = this.targetY - this.entity.getY();
			double l = f * f + j * j + h * h;
			if (l < 2.5000003E-7F)
			{
				this.entity.setForwardSpeed(0.0F);
				return;
			}

			float n = (float)(MathHelper.atan2(h, f) * 180.0F / (float)Math.PI) - 90.0F;

			this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), n, maxYawRate));

			this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
			BlockPos blockPos = this.entity.getBlockPos();
			BlockState blockState = this.entity.getWorld().getBlockState(blockPos);
			VoxelShape voxelShape = blockState.getCollisionShape(this.entity.getWorld(), blockPos);
			if (j > (double)this.entity.getStepHeight() && f * f + h * h < (double)Math.max(1.0F, this.entity.getWidth())
			    || !voxelShape.isEmpty()
			       && this.entity.getY() < voxelShape.getMax(Direction.Axis.Y) + (double)blockPos.getY()
			       && !blockState.isIn(BlockTags.DOORS)
			       && !blockState.isIn(BlockTags.FENCES))
			{
				this.entity.getJumpControl().setActive();
				this.state = SlowTurningMoveControl.State.JUMPING;
			}
		}
		else if (this.state == SlowTurningMoveControl.State.JUMPING)
		{
			this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
			if (this.entity.isOnGround())
			{
				this.state = SlowTurningMoveControl.State.WAIT;
			}
		}
		else
		{
			this.entity.setForwardSpeed(0.0F);
		}
	}
}
