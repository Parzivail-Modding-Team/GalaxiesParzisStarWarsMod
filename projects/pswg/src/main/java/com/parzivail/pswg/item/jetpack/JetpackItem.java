package com.parzivail.pswg.item.jetpack;

import com.parzivail.pswg.item.jetpack.data.JetpackTag;
import com.parzivail.util.item.IDefaultNbtProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class JetpackItem extends ArmorItem implements IDefaultNbtProvider
{
	private final Stats stats;

	public record Stats(float speedSide, float speedHoverAscend, float speedHoverDescend, float speedHoverSlow, float accelVert, float speedVert, float sprintSpeed, float sprintSpeedVert, float fuelUsage, float sprintFuelUsageModifier)
	{
	}

	public JetpackItem(ArmorMaterial material, EquipmentSlot slot, Stats stats, Settings settings)
	{
		super(material, slot, settings);
		this.stats = stats;
	}

	public static ItemStack getEquippedJetpack(LivingEntity entity)
	{
		var stack = entity.getEquippedStack(EquipmentSlot.CHEST);
		if (!stack.isEmpty() && stack.getItem() instanceof JetpackItem)
			return stack;

		// TODO: trinkets

		return ItemStack.EMPTY;
	}

	@Override
	public NbtCompound getDefaultTag(ItemConvertible item, int count)
	{
		return new JetpackTag().toSubtag();
	}

	private static void setVerticalVelocity(LivingEntity player, double y)
	{
		var motion = player.getVelocity();
		player.setVelocity(motion.x, y, motion.z);
	}

	/*
		Based on jetpack code from Iron Jetpacks (MIT licensed)
	 */
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if (!(entity instanceof LivingEntity living))
			return;

		var chest = getEquippedJetpack(living);

		if (!chest.isEmpty())
		{
			var jt = new JetpackTag(chest.getOrCreateNbt());

			if (jt.enabled)
			{
				var isHoldingUp = false;
				var isHoldingDown = false;
				var isHoldingForward = false;
				var isHoldingBackward = false;
				var isHoldingLeft = false;
				var isHoldingRight = false;
				var isHoldingSprint = false;

				if (isHoldingUp || (jt.isHovering && !living.isOnGround()))
				{
					var jetpack = stats;

					var motionY = living.getVelocity().y;
					var hoverSpeed = isHoldingDown ? jetpack.speedHoverDescend : jetpack.speedHoverSlow;
					var currentAccel = jetpack.accelVert * (motionY < 0.3D ? 2.5D : 1.0D);
					var currentSpeedVertical = jetpack.speedVert * (living.isTouchingWater() ? 0.4D : 1.0D);

					var containsEnoughFuel = false;

					if (living instanceof PlayerEntity player && player.isCreative())
						containsEnoughFuel = true;
					else
					{
						var fuelUsage = living.isSprinting() || isHoldingSprint ? jetpack.fuelUsage * jetpack.sprintFuelUsageModifier : jetpack.fuelUsage;

						// TODO: consume fuel
						containsEnoughFuel = true;
					}

					if (containsEnoughFuel)
					{
						double throttle = jt.throttle;
						double verticalSprintMulti = motionY >= 0 && isHoldingSprint ? jetpack.sprintSpeedVert : 1.0D;

						if (isHoldingUp)
						{
							if (!jt.isHovering)
							{
								setVerticalVelocity(living, Math.min(motionY + currentAccel, currentSpeedVertical) * throttle * verticalSprintMulti);
							}
							else
							{
								if (isHoldingDown)
								{
									setVerticalVelocity(living, Math.min(motionY + currentAccel, -jetpack.speedHoverSlow) * throttle);
								}
								else
								{
									setVerticalVelocity(living, Math.min(motionY + currentAccel, jetpack.speedHoverAscend) * throttle * verticalSprintMulti);
								}
							}
						}
						else
						{
							setVerticalVelocity(living, Math.min(motionY + currentAccel, -hoverSpeed) * throttle);
						}

						double speedSideways = (living.isSneaking() ? jetpack.speedSide * 0.5F : jetpack.speedSide) * throttle;
						double speedForward = (living.isSprinting() ? speedSideways * jetpack.sprintSpeed : speedSideways) * throttle;

						if (isHoldingForward)
						{
							living.move(MovementType.PLAYER, new Vec3d(0, 0, speedForward));
						}

						if (isHoldingBackward)
						{
							living.move(MovementType.PLAYER, new Vec3d(0, 0, -speedSideways * 0.8F));
						}

						if (isHoldingLeft)
						{
							living.move(MovementType.PLAYER, new Vec3d(speedSideways, 0, 0));
						}

						if (isHoldingRight)
						{
							living.move(MovementType.PLAYER, new Vec3d(-speedSideways, 0, 0));
						}

						if (!world.isClient)
							living.fallDistance = 0.0F;
					}
				}
			}
		}
	}
}
