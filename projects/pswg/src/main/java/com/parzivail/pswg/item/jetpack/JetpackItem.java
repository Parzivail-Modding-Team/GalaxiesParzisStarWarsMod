package com.parzivail.pswg.item.jetpack;

import com.parzivail.pswg.client.input.IJetpackDataContainer;
import com.parzivail.pswg.client.input.JetpackControls;
import com.parzivail.pswg.item.jetpack.data.JetpackTag;
import com.parzivail.util.item.IDefaultNbtProvider;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class JetpackItem extends TrinketItem implements IDefaultNbtProvider
{
	public record Stats(
			float verticalSpeed,
			float strafeSpeed,
			float strafeHoverSpeed,
			float hoverAscendSpeed,
			float hoverDescendSpeed,
			float verticalAcceleration,
			float strafeSprintSpeed,
			float verticalSprintSpeed,
			float fuelConsumptionPerTick,
			float sprintFuelConsumptionModifier
	)
	{
	}

	private final Stats stats;

	public JetpackItem(Settings settings, Stats stats)
	{
		super(settings);
		this.stats = stats;
	}

	public static ItemStack getEquippedJetpack(LivingEntity entity)
	{
		var t = TrinketsApi.getTrinketComponent(entity);
		if (t.isEmpty())
			return ItemStack.EMPTY;

		var c = t.get();
		var item = c.getEquipped(stack -> stack.getItem() instanceof JetpackItem);
		if (item.isEmpty())
			return ItemStack.EMPTY;

		return item.get(0).getRight();
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

	public static void handeControlPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
	{
		var controls = buf.readShort();
		server.execute(() -> ((IJetpackDataContainer)player).pswg_setJetpackControls(JetpackControls.unpack(controls)));
	}

	/*
			Based on jetpack code from Iron Jetpacks (MIT licensed)
		 */
	@Override
	public void tick(ItemStack stack, SlotReference slot, LivingEntity living)
	{
		var world = living.world;

		if (!stack.isEmpty())
		{
			var jt = new JetpackTag(stack.getOrCreateNbt());

			if (jt.enabled || true)
			{
				var jc = ((IJetpackDataContainer)living);
				var force = jc.pswg_getJetpackForce();
				var controls = jc.pswg_getJetpackControls();

				var isHoldingUp = controls.contains(JetpackControls.ASCEND);
				var isHoldingDown = controls.contains(JetpackControls.DESCEND);
				var isHoldingForward = controls.contains(JetpackControls.FORWARD);
				var isHoldingBackward = controls.contains(JetpackControls.BACKWARD);
				var isHoldingLeft = controls.contains(JetpackControls.LEFT);
				var isHoldingRight = controls.contains(JetpackControls.RIGHT);
				var isHoldingTurbo = controls.contains(JetpackControls.TURBO);

				if (isHoldingUp)
				{
					if (living.isOnGround() || force.equals(Vec3d.ZERO))
						// cancel out falling acceleration, gravity is 0.08 blocks/tick^2
						force = new Vec3d(0, 0.08f, 0);

					force = force.add(new Vec3d(0, 0.004f, 0));

					living.fallDistance = 0;
				}
				else
					force = Vec3d.ZERO;

				jc.pswg_setJetpackForce(new Vec3d(0, MathHelper.clamp(force.y, 0, 0.15f), 0));

				//				if (isHoldingUp || (jt.isHovering && !living.isOnGround()))
				//				{
				//					var velY = living.getVelocity().y;
				//					var hoverSpeed = isHoldingDown ? stats.hoverDescendSpeed : stats.strafeHoverSpeed;
				//					var currentAccel = stats.verticalAcceleration * (velY < 0.3D ? 2.5D : 1.0D);
				//					var currentSpeedVertical = stats.verticalSpeed * (living.isTouchingWater() ? 0.4D : 1.0D);
				//
				//					var containsEnoughFuel = false;
				//
				//					if (living instanceof PlayerEntity player && player.isCreative())
				//						containsEnoughFuel = true;
				//					else
				//					{
				//						var fuelUsage = living.isSprinting() || isHoldingTurbo ? stats.fuelConsumptionPerTick * stats.sprintFuelConsumptionModifier : stats.fuelConsumptionPerTick;
				//
				//						// TODO: consume fuel
				//						jt.serializeAsSubtag(stack);
				//						containsEnoughFuel = true;
				//					}
				//
				//					jt.throttle = 1f;
				//
				//					if (containsEnoughFuel)
				//					{
				//						double throttle = jt.throttle;
				//						var verticalSprintMulti = velY >= 0 && isHoldingTurbo ? stats.verticalSprintSpeed : 1.0D;
				//
				//						if (isHoldingUp)
				//						{
				//							setVerticalVelocity(living, Math.min(Math.max(velY, 0) + 0.08f, 2) * throttle);
				//
				//							if (!jt.isHovering)
				//							{
				//								setVerticalVelocity(living, Math.min(velY + currentAccel, currentSpeedVertical) * throttle * verticalSprintMulti);
				//							}
				//							else
				//							{
				//								if (isHoldingDown)
				//								{
				//									setVerticalVelocity(living, Math.min(velY + currentAccel, -stats.strafeHoverSpeed) * throttle);
				//								}
				//								else
				//								{
				//									setVerticalVelocity(living, Math.min(velY + currentAccel, stats.hoverAscendSpeed) * throttle * verticalSprintMulti);
				//								}
				//							}
				//						}
				//						else
				//						{
				//							setVerticalVelocity(living, Math.min(velY + currentAccel, -hoverSpeed) * throttle);
				//						}
				//
				//						var speedSideways = (living.isSneaking() ? stats.strafeSpeed * 0.5F : stats.strafeSpeed) * throttle;
				//						var speedForward = (living.isSprinting() ? speedSideways * stats.strafeSprintSpeed : speedSideways) * throttle;
				//
				//						if (isHoldingForward)
				//						{
				//							living.travel(new Vec3d(0, 0, speedForward));
				//						}
				//
				//						if (isHoldingBackward)
				//						{
				//							living.travel(new Vec3d(0, 0, -speedSideways * 0.8F));
				//						}
				//
				//						if (isHoldingLeft)
				//						{
				//							living.travel(new Vec3d(speedSideways, 0, 0));
				//						}
				//
				//						if (isHoldingRight)
				//						{
				//							living.travel(new Vec3d(-speedSideways, 0, 0));
				//						}
				//
				//						if (!world.isClient)
				//							living.fallDistance = 0.0F;
				//					}
				//				}
			}
		}
	}
}
